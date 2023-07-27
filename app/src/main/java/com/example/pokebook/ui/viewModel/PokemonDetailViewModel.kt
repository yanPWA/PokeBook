package com.example.pokebook.ui.viewModel

import android.util.Log
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.model.StatType
import com.example.pokebook.repository.DefaultPokemonDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class PokemonDetailViewModel : ViewModel() {
    private var _uiState: MutableStateFlow<PokemonDetailUiState> =
        MutableStateFlow(PokemonDetailUiState.InitialState)
    val uiState = _uiState.asStateFlow()
    private val repository = DefaultPokemonDetailRepository()

    private var _conditionState: MutableStateFlow<PokemonDetailScreenUiData> =
        MutableStateFlow(PokemonDetailScreenUiData())
    val conditionState = _conditionState.asStateFlow()

    /**
     *　ポケモンの種類に関する情報を取得
     */
    fun getPokemonDescription(pokeName: String) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            repository.getPokemonDescription(pokeName)
        }
            .onSuccess {
                val name = it.names.firstOrNull { names -> names.language.name == "ja" }?.name ?: ""

                val description = it.flavorTextEntries.firstOrNull { flavorTextEntries ->
                    flavorTextEntries.language.name == "ja"
                }?.flavorText ?: ""

                val genus =
                    it.genera.firstOrNull { genera -> genera.language.name == "ja" }?.language?.name
                        ?: ""

                val pokemonPersonalData = repository.getPokemonPersonalData(pokeName)

                val type: MutableList<String> =
                    pokemonPersonalData.types.map { type -> type.type.name }.toMutableList()

                pokemonPersonalData.stats.onEach { stats ->
                    _conditionState.update { currentState ->

                        // TODO enumで分岐したい
                        when (stats.stat.name) {
                            "hp" -> {
                                currentState.copy(
                                    hp = stats.baseStat
                                )
                            }

                            "attack" -> currentState.copy(
                                attack = stats.baseStat
                            )

                            "defense" -> currentState.copy(
                                defense = stats.baseStat
                            )

                            "speed" -> currentState.copy(
                                speed = stats.baseStat
                            )
                            else -> currentState
                        }
                    }
                }

                // id 日本語名　説明　属性
                _conditionState.update { currentState ->
                    currentState.copy(
                        id = it.id,
                        name = name,
                        type = type,
                        description = description,
                        genus = genus
                    )
                }
                _uiState.emit(PokemonDetailUiState.Fetched)
            }
            .onFailure {
                Log.d("error", "e[getPokemonList]:$it")
            }
    }
}