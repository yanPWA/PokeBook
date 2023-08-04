package com.example.pokebook.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.repository.DefaultSearchRepository
import com.example.pokebook.ui.screen.convertToJaTypeName
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val DISPLAY_UI_DATA_LIST_ITEM = 20

class SearchViewModel : ViewModel(), DefaultHeader {
    private var _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private val repository = DefaultSearchRepository()

    private var _conditionState: MutableStateFlow<SearchConditionState> =
        MutableStateFlow(SearchConditionState())
    val conditionState = _conditionState.asStateFlow()

    // APIから取得したタイプ別一覧を格納するリスト
    private val responseUiDataList = mutableListOf<List<PokemonListUiData>>()

    // 表示用のタイプ別一覧リスト（20件ずつ）
    private var displayUiDataList = mutableListOf<SearchedListData>()

    /**
     * Type検索
     */
    fun getPokemonByType(typeNumber: String) = viewModelScope.launch {
        _uiState.emit(SearchUiState.Loading)
        // 検索一覧画面のタイトル更新
        _conditionState.update { currentState ->
            currentState.copy(
                pokemonTypeName = typeNumber.convertToJaTypeName(),
            )
        }
        runCatching {
            repository.getPokemonByType(typeNumber)
        }.onSuccess {
            _conditionState.update { currentState ->
                currentState.copy(
                    maxPage = it.pokemon.size.div(DISPLAY_UI_DATA_LIST_ITEM).plus(1).toString(),
                )
            }

            if(it.pokemon.isEmpty()) {
                _uiState.emit(SearchUiState.Fetched(emptyList()))
                return@launch
            }

            val chunkedList = it.pokemon.map { item ->
                PokemonListUiData(
                    name = item.pokemonItem.name,
                    url = item.pokemonItem.url
                )
            }.chunked(DISPLAY_UI_DATA_LIST_ITEM).toMutableList()

            // 最後のリストを調整
            val lastChunk = chunkedList.lastOrNull()?.take(DISPLAY_UI_DATA_LIST_ITEM)
            if (lastChunk != null) {
                chunkedList[chunkedList.size.minus(1)] = lastChunk
            }
            responseUiDataList.clear()
            responseUiDataList += chunkedList
            displayUiDataList = responseUiDataList.map { item ->
                SearchedListData(item, false)
            }.toMutableList()

            updateDisplayUiDataList()
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
        }

    }

    /**
     * 画像url、id、ポケモン日本語名を取得
     */
    private fun updateDisplayUiDataList(pagePosition: Int = 0) = viewModelScope.launch {
        val personalDataList = mutableListOf<PokemonPersonalData>()
        val speciesList = mutableListOf<PokemonSpecies>()
        val urlList = responseUiDataList[pagePosition].map { page -> page.url }

        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = pagePosition,
                isFirst = true
            )
        }

        // 未取得の場合のみ取得しにいく
        if (!displayUiDataList[pagePosition].isFetched) {
            _uiState.emit(SearchUiState.Loading)

            Log.d("test","urlList: $urlList")

            urlList.onEach { url ->
                val pokemonNumber = Uri.parse(url).lastPathSegment
                async {
                    pokemonNumber?.let { number ->
                        // ポケモンのパーソナル情報を取得
                        personalDataList.add(repository.getPokemonPersonalData(number.toInt()))

                        // ポケモンの特性取得のためのNumberを取得
                        val speciesNumber =
                            Uri.parse(personalDataList[personalDataList.lastIndex].species.url).lastPathSegment

                        // ポケモンの特性を取得
                        speciesNumber?.let {
                            speciesList.add(repository.getPokemonSpecies(it.toInt()))
                        }
                    }
                }.await()
            }

            // 取得した情報を表示用listに保存
            responseUiDataList[pagePosition].onEachIndexed { index, responseUiData ->
                responseUiData.apply {
                    imageUrl =
                        personalDataList[index].sprites.other.officialArtwork.imgUrl ?: "".apply {

                        }
                    displayName =
                        speciesList[index].names.firstOrNull { name -> name.language.name == "ja" }?.name
                            ?: ""
                    id = speciesList[index].id
                }
            }
            displayUiDataList[pagePosition] = displayUiDataList[pagePosition].copy(
                list = responseUiDataList[pagePosition],
                isFetched = true
            )
        }
        _uiState.emit(SearchUiState.Fetched(searchList = displayUiDataList[pagePosition].list))
    }

    /**
     * 名前検索
     */
    fun getPokemonByName(name: String) = viewModelScope.launch {
        _uiState.emit(SearchUiState.Loading)
        runCatching {
//            repository.getPokemonPersonalData(name)
        }.onSuccess {
            // TODO 返ってきたURLから個別情報取得する
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
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
     * 初回取得時かどうか
     */
    fun updateIsFirst(isFirst: Boolean) {
        _conditionState.update { current ->
            current.copy(isFirst = isFirst)
        }
    }

    override fun onClickNext() {
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition < responseUiDataList.size.minus(1)) updateDisplayUiDataList(
            conditionState.value.pagePosition.plus(
                1
            )
        )
    }

    override fun onClickBack() {
        val pagePosition = conditionState.value.pagePosition
        if (pagePosition > 0) updateDisplayUiDataList(pagePosition.minus(1))
    }
}

/**
 * 検索一覧表示用
 */
data class SearchedListData(
    val list: List<PokemonListUiData>,
    val isFetched: Boolean
)