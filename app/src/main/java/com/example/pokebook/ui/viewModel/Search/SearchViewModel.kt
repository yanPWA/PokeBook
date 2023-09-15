package com.example.pokebook.ui.viewModel.Search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.searchType.SearchTypeList
import com.example.pokebook.data.searchType.SearchTypeListRepository
import com.example.pokebook.data.searchType.toSearchTypeList
import com.example.pokebook.data.searchType.toSearchTypeListByPokemonListUiData
import com.example.pokebook.repository.ApiSearchRepository
import com.example.pokebook.ui.screen.convertToJaTypeName
import com.example.pokebook.ui.viewModel.DefaultHeader
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import okhttp3.internal.toImmutableList

const val DISPLAY_UI_DATA_LIST_ITEM = 20
const val TYPE_NORMAL = "1"
const val TYPE_FIGHTING = "2"
const val TYPE_FLYING = "3"
const val TYPE_POISON = "4"
const val TYPE_GROUND = "5"
const val TYPE_ROCK = "6"
const val TYPE_BUG = "7"
const val TYPE_GHOST = "8"
const val TYPE_STEEL = "9"
const val TYPE_FIRE = "10"
const val TYPE_WATER = "11"
const val TYPE_GRASS = "12"
const val TYPE_ELECTRIC = "13"
const val TYPE_PSYCHIC = "14"
const val TYPE_ICE = "15"
const val TYPE_DRAGON = "16"
const val TYPE_DARK = "17"
const val TYPE_FAIRY = "18"
const val TYPE_UNKNOWN = "10001"
const val TYPE_SHADOW = "10002"

class SearchViewModel(
    private val searchRepository: ApiSearchRepository,
    private val searchTypeListRepository: SearchTypeListRepository,
    private val pokemonDataRepository: PokemonDataRepository
) : ViewModel(), DefaultHeader {
    private var _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<SearchConditionState> =
        MutableStateFlow(SearchConditionState())
    val conditionState = _conditionState.asStateFlow()

    private val _uiEvent: MutableStateFlow<List<SearchUiEvent>> = MutableStateFlow(listOf())
    val uiEvent: Flow<SearchUiEvent?>
        get() = _uiEvent.map { it.firstOrNull() }

    // イベントの通知
    private fun send(event: SearchUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value + event)
    }

    // イベントの消費
    fun processed(event: SearchUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value.filterNot { it == event })
    }

    // splash画面起動して良いかどうか
    private val _isReady: MutableLiveData<Boolean> = MutableLiveData(false)
    val isReady: LiveData<Boolean> get() = _isReady
    private val responseUiDataList = mutableListOf<PokemonListUiData>()
    private var showUiDataList: List<List<PokemonListUiData>> = emptyList()

    /**
     * 初回起動でAPIからType一覧を取得してDBに保存
     */
    fun getPokemonTypeList() = viewModelScope.launch {
        val typeNumbers = listOf(
            TYPE_NORMAL,
            TYPE_FIGHTING,
            TYPE_FLYING,
            TYPE_POISON,
            TYPE_GROUND,
            TYPE_ROCK,
            TYPE_BUG,
            TYPE_GHOST,
            TYPE_STEEL,
            TYPE_FIRE,
            TYPE_WATER,
            TYPE_GRASS,
            TYPE_ELECTRIC,
            TYPE_PSYCHIC,
            TYPE_ICE,
            TYPE_DRAGON,
            TYPE_DARK,
            TYPE_FAIRY,
            TYPE_UNKNOWN,
            TYPE_SHADOW
        )
        runCatching {
            for (typeNumber in typeNumbers) {
                // APIからタイプ別リストを取得
                val typeList =
                    searchRepository.getPokemonByType(typeNumber).toSearchTypeList()
                // DBに保存
                searchTypeListRepository.insert(typeList)
                _isReady.postValue(true)
                withContext(Dispatchers.IO) {
                    // DBから該当する一覧を取得（全カラム）
                    val roomResult = searchTypeListRepository.searchByTypeNumber(typeNumber.toInt())
                    // 不足データがあればAPIから取得してDB保存する
                    roomResult.forEach { item ->
                        if (item.japaneseName.isNullOrEmpty()) {
                            //APIから取得
                            val pokemonPersonalData =
                                searchRepository.getPokemonPersonalData(item.pokemonNumber)
                            val imageUrl = pokemonPersonalData.sprites.other.officialArtwork.imgUrl
                                ?: ""
                            val speciesNumber =
                                Uri.parse(pokemonPersonalData.species.url).lastPathSegment?.toInt()
                                    ?: 0
                            // DBに保存
                            searchTypeListRepository.updateSpeciesNumberAndImageUrl(
                                pokemonNumber = item.pokemonNumber,
                                imageUrl = imageUrl,
                                speciesNumber = speciesNumber
                            )
                        }
                    }
                }
            }
        }.onSuccess {
            // 何もしない
        }.onFailure {
            _isReady.postValue(false)
            Log.d("error", "e[getPokemonTypeList]：$it") // TODO　いずれエラーダイアログ遷移
        }
    }

    /**
     * タイプ別ボタンが押された時に呼ばれる
     */
    fun onLoad(typeNumber: Int) {
        // タイプボタン押下した時は必ず１ページ目を表示
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = 0
            )
        }
        // type一覧取得
        getPokemonTypeList(typeNumber)
    }

    /**
     * DBから指定されたType一覧を取得して表示
     */
    private fun getPokemonTypeList(typeNumber: Int) = viewModelScope.launch {
        _uiState.emit(SearchUiState.Loading)
        updateIsFirst(true)
        // タイトル名の更新
        _conditionState.update { currentState ->
            currentState.copy(
                pokemonTypeName = typeNumber.toString().convertToJaTypeName()
            )
        }
        runCatching {
            // 表示に関わるListを初期化
            showUiDataList = mutableListOf()

            withContext(Dispatchers.IO) {
                // DBから該当するタイプ一覧を取得
                val roomResult = searchTypeListRepository.searchByTypeNumber(typeNumber)
                    .chunked(DISPLAY_UI_DATA_LIST_ITEM)
                // 表示するデータがない場合は早期return
                if (roomResult.isEmpty()) {
                    showPokemonTypeList()
                    return@withContext
                }
                // maxPageを更新
                _conditionState.update { currentState ->
                    currentState.copy(
                        maxPage = roomResult.size.toString()
                    )
                }
                var japaneseName = ""
                roomResult.forEach { childList ->
                    responseUiDataList.clear()
                    childList.forEach { pokemon ->
                        if (pokemon.japaneseName.isNullOrEmpty()) {
                            // pokemonDataテーブルに存在していない時だけAPIを叩く
                            japaneseName =
                                if (pokemonDataRepository.searchById(pokemon.pokemonNumber) != null) {
                                    pokemonDataRepository.searchById(pokemon.pokemonNumber).japaneseName
                                } else {
                                    searchRepository.getPokemonSpecies(pokemon.speciesNumber).names.firstOrNull { name -> name.language.name == "ja" }?.name
                                        ?: ""
                                }
                            responseUiDataList.add(
                                PokemonListUiData(
                                    pokemonNumber = pokemon.pokemonNumber,
                                    displayName = japaneseName,
                                    imageUrl = pokemon.imageUrl ?: "",
                                    speciesNumber = pokemon.speciesNumber.toString()
                                )
                            )
                        } else {
                            // DBに存在している時はDBの情報を取得
                            responseUiDataList.add(
                                PokemonListUiData(
                                    pokemonNumber = pokemon.pokemonNumber,
                                    displayName = pokemon.japaneseName,
                                    imageUrl = pokemon.imageUrl ?: "",
                                    speciesNumber = pokemon.speciesNumber.toString()
                                )
                            )
                        }
                    }
                    val mutableList = showUiDataList.toMutableList()
                    mutableList.add(responseUiDataList.toImmutableList())
                    showUiDataList = mutableList
                    showPokemonTypeList()
                }
            }
        }.onSuccess {
            // DB保存
            saveSearchTypeResult(showUiDataList)
        }.onFailure {
            send(SearchUiEvent.Error(it))
            _uiState.emit(SearchUiState.ResultError)
            Log.d("test", "e[getPokemonTypeList]：$it")
        }
    }

    /**
     * 指定したpagePositionのリストを表示
     */
    private fun showPokemonTypeList(pagePosition: Int = 0) = viewModelScope.launch {
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = pagePosition,
            )
        }

        try {
            _uiState.emit(
                SearchUiState.Fetched(
                    searchList = if (showUiDataList.isNotEmpty()) {
                        showUiDataList[conditionState.value.pagePosition].toImmutableSet()
                            .toImmutableList() // TODO 謎。いつか調べたい。一発で.toImmutableList()で変換したい
                    } else {
                        persistentListOf()
                    }
                )
            )
        } catch (e: Exception) {
            Log.d("test", "e[showPokemonTypeList]:$e")
        }
    }

    /**
     * 作成したタイプ別一覧をDBに保存
     */
    private fun saveSearchTypeResult(resultList: List<List<PokemonListUiData>>) =
        viewModelScope.launch {
            var saveList: List<List<SearchTypeList>>
            withContext(Dispatchers.IO) {
                // DB保存用データ型に変換
                saveList = resultList.toMutableList().map { childList ->
                    childList.toSearchTypeListByPokemonListUiData()
                }
                // DBに保存
                saveList.forEach { childList ->
                    childList.forEach { pokemon ->
                        searchTypeListRepository.updateJapaneseName(
                            pokemonNumber = pokemon.pokemonNumber,
                            japaneseName = pokemon.japaneseName ?: ""
                        )
                    }
                }
            }
        }

    /**
     * どのボタンが押下されたかを更新
     */
    fun updateButtonStates(
        isBackButton: Boolean = false,
        isNextButton: Boolean = false
    ) {
        _conditionState.update { currentState ->
            currentState.copy(
                isBackButton = isBackButton,
                isNextButton = isNextButton
            )
        }
    }

    /**
     * スクロールを先頭に戻すかどうか
     */
    fun updateIsFirst(isScrollTop: Boolean) {
        _conditionState.update { current ->
            current.copy(isScrollTop = isScrollTop)
        }
    }

    override fun onClickNext() {
        updateIsFirst(true)
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition < responseUiDataList.size.minus(1)) showPokemonTypeList(
            conditionState.value.pagePosition.plus(
                1
            )
        )
    }

    override fun onClickBack() {
        updateIsFirst(true)
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition > 0) showPokemonTypeList(pagePosition.minus(1))
    }
}
