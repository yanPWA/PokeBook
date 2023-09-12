package com.example.pokebook.ui.viewModel.Search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.searchType.SearchTypeListRepository
import com.example.pokebook.data.searchType.toSearchTypeList
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

    // APIから取得したタイプ別一覧を格納するリスト
    private val responseUiDataList = mutableListOf<List<PokemonListUiData>>()

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

    private val searchedTypeList = mutableListOf<SearchedType>()

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
            _isReady.postValue(true)
        }.onFailure {
            _isReady.postValue(false)
            Log.d("error", "e[getPokemonTypeList]：$it") // TODO　いずれエラーダイアログ遷移
        }
    }

    /**
     * DBから指定されたType一覧を取得して表示
     */
    fun showPokemonTypeList(typeNumber: Int) = viewModelScope.launch {
        // リスト取得済みかどうかを確認
        searchedTypeList.forEach { item ->
            if (item.typeNumber == typeNumber && item.isFetched) return@launch
        }

        _uiState.emit(SearchUiState.Loading)
        // タイトル名の更新
        _conditionState.update { currentState ->
            currentState.copy(
                pokemonTypeName = typeNumber.toString().convertToJaTypeName()
            )
        }
        runCatching {
            withContext(Dispatchers.IO) {
                // DBから該当する一覧を取得（全カラム）
                val roomResult = searchTypeListRepository.searchByTypeNumber(typeNumber)
                // maxPageを更新
                _conditionState.update { currentState ->
                    currentState.copy(
                        maxPage = roomResult.size.div(DISPLAY_UI_DATA_LIST_ITEM).plus(1).toString()
                    )
                }

                // 不足データがあればAPIから取得してDB保存する
                roomResult.forEach { pokemon ->
                    if (pokemon.japaneseName.isNullOrEmpty()) {
                        //APIから取得
                        val japaneseName =
                            searchRepository.getPokemonSpecies(pokemon.speciesNumber).names.firstOrNull { name -> name.language.name == "ja" }?.name
                                ?: ""

                        // DBに保存
                        searchTypeListRepository.updateJapaneseName(
                            pokemonNumber = pokemon.pokemonNumber,
                            japaneseName = japaneseName
                        )
                    }
                }
                // 取得したListを20件ずつのListに変換
                val updateRoomResult = searchTypeListRepository.searchByTypeNumber(typeNumber)
                // 20件ずつに分割したリストを生成
                val chunkedList = updateRoomResult.map { item ->
                    PokemonListUiData(
                        pokemonNumber = item.pokemonNumber,
                        displayName = item.japaneseName ?: "",
                        imageUrl = item.imageUrl,
                        speciesNumber = item.speciesNumber.toString()
                    )
                }.chunked(DISPLAY_UI_DATA_LIST_ITEM).toMutableList()

                // 最後のリストを調整
                val lastChunkList = chunkedList.lastOrNull()?.take(DISPLAY_UI_DATA_LIST_ITEM)
                if (lastChunkList != null) {
                    chunkedList[chunkedList.size.minus(1)] = lastChunkList
                }
                responseUiDataList.clear()
                responseUiDataList += chunkedList
            }
        }.onSuccess {
            // 該当するListを表示
            showUiDataList()
            searchedTypeList.add(
                SearchedType(
                    typeNumber = typeNumber,
                    isFetched = true
                )
            )
        }.onFailure {
            send(SearchUiEvent.Error(it))
            _uiState.emit(SearchUiState.ResultError)
            Log.d("test", "e[showPokemonTypeList]：$it")
        }
    }

    /**
     * 指定したpagePositionのリストを表示
     */
    private fun showUiDataList(pagePosition: Int = 0) = viewModelScope.launch {
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = pagePosition,
                isFirst = true,
            )
        }

        _uiState.emit(
            SearchUiState.Fetched(
                searchList = if (responseUiDataList.size != 0) {
                    responseUiDataList[pagePosition]
                } else {
                    emptyList()
                }
            )
        )
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
     * 初回取得時かどうか
     */
    fun updateIsFirst(isFirst: Boolean) {
        _conditionState.update { current ->
            current.copy(isFirst = isFirst)
        }
    }

    override fun onClickNext() {
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition < responseUiDataList.size.minus(1)) showUiDataList(
            conditionState.value.pagePosition.plus(
                1
            )
        )
    }

    override fun onClickBack() {
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition > 0) showUiDataList(pagePosition.minus(1))
    }
}

/**
 * 検索一覧表示用
 */
data class SearchedType(
    val typeNumber: Int,
    val isFetched: Boolean
)
