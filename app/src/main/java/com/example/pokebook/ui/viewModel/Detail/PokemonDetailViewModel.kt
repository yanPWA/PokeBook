package com.example.pokebook.ui.viewModel.Detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.like.LikesRepository
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.StatType
import com.example.pokebook.repository.PokemonDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonDetailViewModel(
    private val detailRepository: PokemonDetailRepository,
    private val dataRepository: PokemonDataRepository,
    private val likesRepository: LikesRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<PokemonDetailUiState> =
        MutableStateFlow(PokemonDetailUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<PokemonDetailScreenUiData> =
        MutableStateFlow(PokemonDetailScreenUiData())
    val conditionState = _conditionState.asStateFlow()

    private val _uiEvent: MutableStateFlow<List<PokemonDetailUiEvent>> = MutableStateFlow(listOf())
    val uiEvent: Flow<PokemonDetailUiEvent?>
        get() = _uiEvent.map { it.firstOrNull() }

    // イベントの通知
    private fun send(event: PokemonDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value + event)
    }

    // イベントの消費
    fun processed(event: PokemonDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value.filterNot { it == event })
    }


    private lateinit var species: PokemonSpecies

    /**
     *　ポケモンの種類に関する情報を取得（番号検索）
     */
    fun getPokemonSpeciesById(pokeId: Int) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            detailRepository.getPokemonPersonalData(pokeId)
        }.onSuccess {
            val speciesNumber = Uri.parse(it.species.url).lastPathSegment

            speciesNumber?.let { num ->
                // ポケモン特性を取得
                species = detailRepository.getPokemonSpecies(num.toInt())
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
                    pokemonNumber = species.id,
                    name = name,
                    type = type,
                    description = description,
                    genus = genus,
                    imageUri = imageUri ?: "",
                    height = it.height / 10.0,
                    weight = it.weight / 10.0
                )
            }
            _uiState.emit(PokemonDetailUiState.Fetched(detailUiCondition = DetailUiCondition()))
        }.onFailure {
            send(PokemonDetailUiEvent.Error(it))
            _uiState.emit(PokemonDetailUiState.ResultError)
        }
    }

    /**
     * ポケモンの種類に関する情報を取得（名前検索）
     */
    fun getPokemonSpeciesByName(searchName: String) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            withContext(Dispatchers.IO) {
                val result = dataRepository.searchPokemonByKeyword(searchName)
                val searchId = if (searchName == result.japaneseName) result.id else null
                searchId?.let {
                    _conditionState.update { currentState ->
                        currentState.copy(
                            pokemonNumber = it
                        )
                    }
                }
            }
        }.onSuccess {
            getPokemonSpeciesById(conditionState.value.pokemonNumber)
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
            send(PokemonDetailUiEvent.Error(it))
            _uiState.emit(PokemonDetailUiState.SearchError)
        }
    }

    /**
     * Likeフラグを更新
     */
    fun updateIsLike(isLike: Boolean, pokemonNumber: Int) = viewModelScope.launch {
        if (conditionState.value.pokemonNumber == pokemonNumber) {
            _conditionState.update { currentState ->
                currentState.copy(
                    isLike = isLike
                )
            }
        }

        _uiState.emit(
            PokemonDetailUiState.Fetched(
                DetailUiCondition(
                    isLike = isLike
                )
            )
        )
    }

    /**
     * RoomのLikeテーブルに該当ポケモンが存在するかどうか
     */
    suspend fun checkIfRoomLike(pokemonNumber: Int) {
        runCatching {
            withContext(Dispatchers.IO) {
                likesRepository.searchPokemonByName(pokemonNumber)
            }
        }
            .onSuccess {
                _uiState.emit(
                    PokemonDetailUiState.Fetched(
                        detailUiCondition = DetailUiCondition(isLike = it != null && it.pokemonNumber == pokemonNumber)
                    )
                )
                _conditionState.update { currentState ->
                    currentState.copy(
                        isLike = it != null && it.pokemonNumber == pokemonNumber
                    )
                }
            }
            .onFailure {
                Log.d("error", "error：$it")
            }
    }
}
