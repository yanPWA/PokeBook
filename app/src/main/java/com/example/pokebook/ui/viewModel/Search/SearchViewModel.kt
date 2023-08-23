package com.example.pokebook.ui.viewModel.Search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.StatType
import com.example.pokebook.repository.PokemonDetailRepository
import com.example.pokebook.repository.SearchRepository
import com.example.pokebook.ui.screen.convertToJaTypeName
import com.example.pokebook.ui.viewModel.DefaultHeader
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailScreenUiData
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailUiState
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DISPLAY_UI_DATA_LIST_ITEM = 20

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel(),
    DefaultHeader {
    private var _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<SearchConditionState> =
        MutableStateFlow(SearchConditionState())
    val conditionState = _conditionState.asStateFlow()

    // APIから取得したタイプ別一覧を格納するリスト
    private val responseUiDataList = mutableListOf<List<PokemonListUiData>>()

    // 表示用のタイプ別一覧リスト（20件ずつ）
    private var displayUiDataList = mutableListOf<SearchedListData>()

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
            searchRepository.getPokemonByType(typeNumber)
        }.onSuccess {
            _conditionState.update { currentState ->
                currentState.copy(
                    maxPage = it.pokemon.size.div(DISPLAY_UI_DATA_LIST_ITEM).plus(1).toString(),
                )
            }

            if (it.pokemon.isEmpty()) {
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
            send(SearchUiEvent.Error(it))
            _uiState.emit(SearchUiState.ResultError)
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
            urlList.onEach { url ->
                val pokemonNumber = Uri.parse(url).lastPathSegment
                async {
                    pokemonNumber?.let { number ->
                        // ポケモンのパーソナル情報を取得
                        personalDataList.add(searchRepository.getPokemonPersonalData(number.toInt()))

                        // ポケモンの特性取得のためのNumberを取得
                        val speciesNumber =
                            Uri.parse(personalDataList[personalDataList.lastIndex].species.url).lastPathSegment

                        // ポケモンの特性を取得
                        speciesNumber?.let {
                            speciesList.add(searchRepository.getPokemonSpecies(it.toInt()))
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
                    pokemonNumber = speciesList[index].id
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
