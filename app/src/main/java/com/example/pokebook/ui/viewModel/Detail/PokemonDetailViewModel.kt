package com.example.pokebook.ui.viewModel.Detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.StatType
import com.example.pokebook.repository.DefaultPokemonDetailRepository
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel : ViewModel() {
    private var _uiState: MutableStateFlow<PokemonDetailUiState> =
        MutableStateFlow(PokemonDetailUiState.InitialState)
    val uiState = _uiState.asStateFlow()
    private val repository = DefaultPokemonDetailRepository()

    private var _conditionState: MutableStateFlow<PokemonDetailScreenUiData> =
        MutableStateFlow(PokemonDetailScreenUiData())
    val conditionState = _conditionState.asStateFlow()

    private lateinit var species: PokemonSpecies

    /**
     *　ポケモンの種類に関する情報を取得（番号検索）
     */
    fun getPokemonSpeciesByNumber(pokeId: Int) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            repository.getPokemonPersonalData(pokeId)
        }.onSuccess {
            val speciesNumber = Uri.parse(it.species.url).lastPathSegment

            speciesNumber?.let { num ->
                // ポケモン特性を取得
                species = repository.getPokemonSpecies(num.toInt())
            }
            // 名前
            val name =
                species.names.firstOrNull { names -> names.language.name == "ja" }?.name ?: ""

            // 説明
            val description = species.flavorTextEntries.firstOrNull { flavorTextEntries ->
                flavorTextEntries.language.name == "ja"
            }?.flavorText ?: ""

            // 分類
            val genus =
                species.genera.firstOrNull { genera -> genera.language.name == "ja" }?.genus ?: ""

            // タイプ
            val type: MutableList<String> =
                it.types.map { type -> type.type.name }.toMutableList()

            // 画像
            val imageUri = it.sprites.other.officialArtwork.imgUrl

            it.stats.onEach { stats ->
                _conditionState.update { currentState ->
                    when (stats.stat.name) {
                        StatType.HP.type -> {
                            currentState.copy(
                                hp = stats.baseStat
                            )
                        }

                        StatType.ATTACK.type -> currentState.copy(
                            attack = stats.baseStat
                        )

                        StatType.DEFENSE.type -> currentState.copy(
                            defense = stats.baseStat
                        )

                        StatType.SPEED.type -> currentState.copy(
                            speed = stats.baseStat
                        )

                        else -> currentState
                    }
                }
            }

            // id 日本語名　説明　属性
            _conditionState.update { currentState ->
                currentState.copy(
                    id = species.id,
                    name = name,
                    type = type,
                    description = description,
                    genus = genus,
                    imageUri = imageUri ?: "",
                    height = it.height / 10.0,
                    weight = it.weight / 10.0
                )
            }
            _uiState.emit(PokemonDetailUiState.Fetched)
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
        }
    }

    /**
     * ポケモンの種類に関する情報を取得（画像URLを引数で渡す）
     */
    fun getPokemonSpeciesByNumber(pokemonListUiData: PokemonListUiData) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            repository.getPokemonPersonalData(pokemonListUiData.id)
        }.onSuccess {
            val speciesNumber = Uri.parse(it.species.url).lastPathSegment
            speciesNumber?.let { num ->
                // ポケモン特性を取得
                species = repository.getPokemonSpecies(num.toInt())
            }

            // 説明
            val description = species.flavorTextEntries.firstOrNull { flavorTextEntries ->
                flavorTextEntries.language.name == "ja"
            }?.flavorText ?: ""

            // 分類
            val genus =
                species.genera.firstOrNull { genera -> genera.language.name == "ja" }?.genus ?: ""

            // タイプ
            val type: MutableList<String> =
                it.types.map { type -> type.type.name }.toMutableList()

            // HP 攻撃　防御　スピード
            it.stats.onEach { stats ->
                _conditionState.update { currentState ->
                    when (stats.stat.name) {
                        StatType.HP.type -> {
                            currentState.copy(
                                hp = stats.baseStat
                            )
                        }

                        StatType.ATTACK.type -> currentState.copy(
                            attack = stats.baseStat
                        )

                        StatType.DEFENSE.type -> currentState.copy(
                            defense = stats.baseStat
                        )

                        StatType.SPEED.type -> currentState.copy(
                            speed = stats.baseStat
                        )

                        else -> currentState
                    }
                }
            }

            // id 日本語名　説明　属性
            _conditionState.update { currentState ->
                currentState.copy(
                    id = pokemonListUiData.id,
                    name = pokemonListUiData.displayName,
                    type = type,
                    description = description,
                    genus = genus,
                    imageUri = pokemonListUiData.imageUrl ?: "",
                    height = it.height / 10.0,
                    weight = it.weight / 10.0
                )
            }
            _uiState.emit(PokemonDetailUiState.Fetched)
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
        }
    }
}
