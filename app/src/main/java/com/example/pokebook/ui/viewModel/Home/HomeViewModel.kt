package com.example.pokebook.ui.viewModel.Home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.pokemonData.pokemonPersonalDataToPokemonData
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.repository.HomeRepository
import com.example.pokebook.ui.viewModel.DefaultHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository,
    private val pokemonDataRepository: PokemonDataRepository
) : ViewModel(), DefaultHeader {
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

    private val uiDataList = mutableListOf<PokemonListUiData>()

    init {
        if (uiDataList.isEmpty()) getPokemonList()
    }

    /**
     * ポケモンリスト取得
     */
    fun getPokemonList() = viewModelScope.launch {
        _uiState.emit(HomeUiState.Loading)
        updateIsFirst(true)
        uiDataList.clear()
        val dbList = getAllItemsBetweenIds()
        runCatching {
            for (index in dbList) {
                if (index.imageUrl?.isEmpty() == true) {
                    // DBに画像URL情報がない場合はAPIから取得してくる
                    val pokemonPersonalData = getPokemonPersonalData(index.pokemonNumber)
                    pokemonPersonalData.imageUrl?.let {
                        updatePokemonData(
                            indexId = index.pokemonNumber,
                            pokemonPersonalData = pokemonPersonalData
                        )
                    }
                    uiDataList += PokemonListUiData(
                        pokemonNumber = index.pokemonNumber,
                        displayName = index.japaneseName,
                        imageUrl = pokemonPersonalData.imageUrl,
                        speciesNumber = pokemonPersonalData.speciesNumber
                    )
                } else {
                    uiDataList += PokemonListUiData(
                        pokemonNumber = index.pokemonNumber,
                        displayName = index.japaneseName,
                        imageUrl = index.imageUrl,
                        speciesNumber = index.speciesNumber
                    )
                }
            }
        }.onSuccess {
            _uiState.emit(HomeUiState.Fetched(uiDataList = uiDataList))
        }.onFailure {
            send(HomeUiEvent.Error(it))
            _uiState.emit(HomeUiState.ResultError)
        }
    }

    /**
     * DBからリストを取得(20件)
     */
    private suspend fun getAllItemsBetweenIds(): List<PokemonData> {
        return withContext(Dispatchers.IO) {
            pokemonDataRepository.getAllItemsBetweenIds(
                startId = conditionState.value.pagePosition * 20 + 1,
                endId = conditionState.value.pagePosition * 20 + 20
            )
        }
    }

    /**
     * APIから画像URLを取得
     */
    private suspend fun getPokemonPersonalData(indexId: Int): PokemonData {
        return withContext(Dispatchers.IO) {
            repository.getPokemonPersonalData(indexId).pokemonPersonalDataToPokemonData()
        }
    }

    /**
     * DBに情報を更新
     */
    private suspend fun updatePokemonData(indexId: Int, pokemonPersonalData: PokemonData) {
        withContext(Dispatchers.IO) {
            pokemonDataRepository.updatePokemonData(
                id = indexId,
                imageUrl = pokemonPersonalData.imageUrl ?: "",
                speciesNumber = pokemonPersonalData.speciesNumber
            )
        }
    }

    /**
     * 「次へ」ボタン押下してポケモンリスト取得
     */
    override fun onClickNext() {
        updateIsFirst(true)
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = conditionState.value.pagePosition.plus(1)
            )
        }
        getPokemonList()
    }

    /**
     * 「戻る」ボタン押下して一つ前のポケモンリストを取得
     */
    override fun onClickBack() {
        updateIsFirst(true)
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = conditionState.value.pagePosition.minus(1)
            )
        }
        getPokemonList()
    }

    /**
     * スクロールを先頭に戻すかどうか
     */
    fun updateIsFirst(isScrollTop: Boolean) {
        _conditionState.update { current ->
            current.copy(isScrollTop = isScrollTop)
        }
    }
}
