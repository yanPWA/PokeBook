package com.example.pokebook.ui.viewModel.Home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.model.Pokemon
import com.example.pokebook.repository.DefaultHomeRepository
import com.example.pokebook.ui.viewModel.DefaultHeader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(), DefaultHeader {
    private var _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<HomeScreenConditionState> =
        MutableStateFlow(HomeScreenConditionState())
    val conditionState = _conditionState.asStateFlow()

    private val _uiEvent: MutableStateFlow<List<HomeUiEvent>> = MutableStateFlow(listOf())
    val uiEvent: Flow<HomeUiEvent?>
        get() = _uiEvent.map { it.firstOrNull() }

    // イベントの通知
    private fun send(event: HomeUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value + event)
    }

    // イベントの消費
    fun processed(event: HomeUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value.filterNot { it == event })
    }

    private val repository = DefaultHomeRepository()
    private val uiDataList = mutableListOf<PokemonListUiData>()

    init {
        getPokemonList(true)
    }

    /**
     * ポケモンリスト取得
     */
    fun getPokemonList(isFirst: Boolean, isBackButton: Boolean = false) {
        viewModelScope.launch {
            _uiState.emit(HomeUiState.Loading)
            updateIsFirst(true)
            runCatching {
                if (isFirst) {
                    repository.getPokemonList()
                } else {
                    when (isBackButton) {
                        true -> repository.getPokemonList(conditionState.value.previous)
                        false -> repository.getPokemonList(conditionState.value.offset)
                    }
                }
            }
                .onSuccess {
                    uiDataList.clear()
                    updateConditionState(it)
                    uiDataList += it.results.map { item ->
                        PokemonListUiData(
                            name = item.name,
                            url = item.url
                        )
                    }.toMutableList()
                    // 一覧に表示する画像を取得
                    val pokemonPersonalDataList = it.results.map { item ->
                        val pokemonNumber = Uri.parse(item.url).lastPathSegment
                        async {
                            pokemonNumber?.let { number ->
                                // ポケモンのパーソナル情報を取得
                                repository.getPokemonPersonalData(number.toInt())
                            }
                        }
                    }.awaitAll()
                    uiDataList.onEachIndexed { index, item ->
                        item.imageUrl =
                            pokemonPersonalDataList[index]?.sprites?.other?.officialArtwork?.imgUrl
                                ?: ""
                    }

                    // 一覧に表示するポケモンの日本語名を取得
                    val pokemonSpeciesList = it.results.map { item ->
                        val pokemonNumber = Uri.parse(item.url).lastPathSegment
                        async {
                            pokemonNumber?.let { number ->
                                val data = repository.getPokemonPersonalData(number.toInt())
                                val speciesNumber = Uri.parse(data.species.url).lastPathSegment
                                speciesNumber?.let { num ->
                                    repository.getPokemonSpecies(num.toInt())
                                }
                            }
                        }
                    }.awaitAll()
                    uiDataList.onEachIndexed { index, item ->
                        item.displayName =
                            pokemonSpeciesList[index]?.names?.firstOrNull { name -> name.language.name == "ja" }?.name
                                ?: ""
                        item.id = pokemonSpeciesList[index]?.id ?: 0
                    }
                    _uiState.emit(HomeUiState.Fetched(uiDataList = uiDataList))
                }
                .onFailure {
                    send(HomeUiEvent.Error(it))
                    _uiState.emit(HomeUiState.ResultError)
                }
        }
    }

    /**
     * 「次へ」ボタン押下してポケモンリスト取得
     */
    override fun onClickNext() {
        getPokemonList(isFirst = false)
    }

    /**
     * 「戻る」ボタン押下して一つ前のポケモンリストを取得
     */
    override fun onClickBack() {
        getPokemonList(isFirst = false, isBackButton = true)
    }

    /**
     * nextUrlからクエリを取得してOffsetを更新する
     */
    private fun updateConditionState(pokemon: Pokemon) {
        val offsetValue = Uri.parse(pokemon.next).getQueryParameter("offset") ?: return
        val previousValue = pokemon.previous?.let {
            Uri.parse(it).getQueryParameter("offset")
        }
        _conditionState.update { currentState ->
            currentState.copy(
                count = pokemon.count,
                offset = offsetValue,
                previous = previousValue ?: "",
                currentNumberStart = offsetValue.toInt().minus(19).toString()
            )
        }
    }

    /**
     * 初回取得時かどうか
     */
    fun updateIsFirst(isScrollTop: Boolean) {
        _conditionState.update { current ->
            current.copy(isScrollTop = isScrollTop)
        }
    }
}
