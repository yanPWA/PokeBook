package com.example.pokebook.ui.viewModel.Home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.pokemonData.pokemonPersonalDataToPokemonData
import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.repository.DefaultHomeRepository
import com.example.pokebook.repository.HomeRepository
import com.example.pokebook.ui.viewModel.DefaultHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        getPokemonList()
    }
    /**
     * ポケモンリスト取得
     */
    fun getPokemonList() {
        viewModelScope.launch {
            _uiState.emit(HomeUiState.Loading)
            updateIsFirst(true)
            runCatching {
                withContext(Dispatchers.IO) {
                    // DBからとってきて中身をチェックする（20件ずつ）
                    pokemonDataRepository.getAllItemsBetweenIds(
                        startId = conditionState.value.pagePosition * 20 + 1,
                        endId = conditionState.value.pagePosition * 20 + 20
                    ).apply {
                        uiDataList.clear()
                        for (index in this) {
                            // imageUrlがなければAPIを叩く
                            if (index.imageUrl?.isEmpty() == true) {
                                val pokemonPersonalData =
                                    repository.getPokemonPersonalData(index.id)
                                        .pokemonPersonalDataToPokemonData()

                                async{
                                    // imageUrlを取得したらDBに保存する
                                    pokemonPersonalData.imageUrl?.let{
                                        pokemonDataRepository.updatePokemonData(
                                            id = index.id,
                                            imageUrl = pokemonPersonalData.imageUrl,
                                            speciesNumber = pokemonPersonalData.speciesNumber
                                        )
                                    }

                                    // 表示用リストに追加する
                                    uiDataList += PokemonListUiData(
                                        pokemonNumber = index.id,
                                        displayName = index.japaneseName,
                                        imageUrl = pokemonPersonalData.imageUrl
                                    )
                                }.await()

                                // HomeScreenConditionStateを更新
                                _conditionState.update { currentState ->
                                    currentState.copy(
                                        speciesNumber = pokemonPersonalData.speciesNumber
                                    )
                                }
                            }else {
                                uiDataList += PokemonListUiData(
                                    pokemonNumber = index.id,
                                    displayName = index.japaneseName,
                                    imageUrl = index.imageUrl
                                )
                            }
                        }
                    }
                }
            }
                .onSuccess {
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
        _conditionState.update { currentState ->
            currentState.copy(
                pagePosition = conditionState.value.pagePosition.minus(1)
            )
        }
        getPokemonList()
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
