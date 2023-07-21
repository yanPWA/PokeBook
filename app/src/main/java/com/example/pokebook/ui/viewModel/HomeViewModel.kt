package com.example.pokebook.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.repository.DefaultHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var _pokeListUiState: MutableStateFlow<MutableList<PokemonUiData>> =
        MutableStateFlow(mutableListOf())
    val pokeListUiState = _pokeListUiState.asStateFlow()
    private val repository = DefaultHomeRepository()

//    var apiState: ApiState by mutableStateOf(ApiState.Loading)
//        private set

    init {
        getPokemonList()
    }

    /**
     * ポケモンリスト取得
     */
    private fun getPokemonList() {
        viewModelScope.launch {
            runCatching {
                repository.getPokemonList()
//                PokeApi.retrofitService.getPokemonList()
            }
                .onSuccess {
                    _pokeListUiState.value = it.results.map { listItem ->
                        PokemonUiData(
                            name = listItem.name,
                            url = listItem.url
                        )
                    }.toMutableList()

                    pokeListUiState.value.forEach { item ->
                        val pokemonNumber = Uri.parse(item.url).lastPathSegment

                        // ポケモンNo.が取得できたらポケモン個体情報を取得する
                        pokemonNumber?.let { number ->
                            getPokemonPersonalData(
                                number = number,
                                pokemonUiData = item
                            )
                        }
                    }
//                    apiState = ApiState.Success(it)
                }
                .onFailure {
//                    apiState = ApiState.Error
                    Log.d("error", "e[getPokemonList]:$it")
                }
        }
    }

    /**
     * 個別のポケモン情報取得
     */
    private fun getPokemonPersonalData(number: String, pokemonUiData: PokemonUiData) {
        viewModelScope.launch {
            runCatching {
                repository.getPokemonPersonalData(number)
//                PokeApi.retrofitService.getPokemonPersonalData(number)
            }
                .onSuccess {
                    pokemonUiData.imageUri = it.sprites.other.officialArtwork.imgUrl
                }
                .onFailure {
                    Log.d("error", "e[getPokemonPersonalData]:$it")
                }
        }
    }
}