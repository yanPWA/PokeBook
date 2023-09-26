package com.example.pokebook.ui.viewModel.Detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.like.LikesRepository
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.model.EvolutionChain
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.StatType
import com.example.pokebook.repository.DefaultEvolutionChainRepository
import com.example.pokebook.repository.EvolutionChainRepository
import com.example.pokebook.repository.PokemonDetailRepository
import com.example.pokebook.ui.screen.ShowEvolution
import com.example.pokebook.ui.screen.convertToShowEvolution
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
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
    private val likesRepository: LikesRepository,
    private val pokemonDataRepository: PokemonDataRepository,
    private val evolutionChainRepository: EvolutionChainRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<PokemonDetailUiState> =
        MutableStateFlow(PokemonDetailUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<PokemonDetailScreenUiData> =
        MutableStateFlow(PokemonDetailScreenUiData())
    val conditionState = _conditionState.asStateFlow()

    private var _pokemonListUiDataConditionState: MutableStateFlow<PokemonListUiData> =
        MutableStateFlow(PokemonListUiData())

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

    /**
     *　ポケモンの種類に関する情報を取得
     */
    fun getPokemonSpeciesById(
        pokemonNumber: Int,
        speciesNumber: Int = 0
    ) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            withContext(Dispatchers.IO) {
                // DBを検索
                val roomResult = pokemonDataRepository.searchById(pokemonNumber)

                // DBにデータが存在していなければAPIを叩く
                if (roomResult == null) {
                    val result = getApiPokemonData(pokemonNumber, speciesNumber)
                    // 必要情報を取得してconditionStateを更新
                    updateCondition(
                        apiResult = result.personalData,
                        species = result.species
                    )

                    // 新規でDBに保存
                    saveDataBase(
                        roomResult = roomResult,
                        pokemonNumber = pokemonNumber
                    )

                    // DBにdescriptionがない場合
                } else if (roomResult.description.isNullOrEmpty()) {
                    val result = getApiPokemonData(pokemonNumber, speciesNumber)
                    // 必要情報を取得してconditionStateを更新
                    updateCondition(
                        roomResult = roomResult,
                        apiResult = result.personalData,
                        species = result.species
                    )

                    // pokemonNumberに該当するDBの情報を更新
                    saveDataBase(
                        roomResult = roomResult,
                        pokemonNumber = pokemonNumber
                    )
                } else {
                    //conditionStateを更新
                    _conditionState.update { currentState ->
                        currentState.copy(
                            pokemonNumber = roomResult.pokemonNumber,
                            englishName = roomResult.englishName ?: "",
                            japaneseName = roomResult.japaneseName,
                            description = roomResult.description,
                            hp = roomResult.hp ?: 0,
                            attack = roomResult.attack ?: 0,
                            defense = roomResult.defense ?: 0,
                            speed = roomResult.speed ?: 0,
                            imageUri = roomResult.imageUrl ?: "",
                            type = roomResult.type ?: emptyList(),
                            genus = roomResult.genus ?: "",
                            speciesNumber = roomResult.speciesNumber ?: "",
                            evolutionChainNumber = roomResult.evolutionChainNumber ?: ""
                        )
                    }
                    // pokemonNumberに該当するDBの情報を更新
                    saveDataBase(
                        roomResult = roomResult,
                        pokemonNumber = pokemonNumber
                    )
                }

                //進化系譜取得
                _conditionState.update { currentState ->
                    currentState.copy(
                        resultEvolutionChain = evolutionChainRepository.getPokemonEvolutionChain(
                            conditionState.value.evolutionChainNumber
                        )
                    )
                }
            }
        }.onSuccess {
            _uiState.emit(PokemonDetailUiState.Fetched(detailUiCondition = DetailUiCondition()))
        }.onFailure {
            send(PokemonDetailUiEvent.Error(it))
            _uiState.emit(PokemonDetailUiState.ResultError)
        }
    }

    /**
     * APIから詳細情報を取得
     */
    private suspend fun getApiPokemonData(
        pokemonNumber: Int,
        speciesNumber: Int,
    ): ApiResult {
        var localSpeciesNumber = speciesNumber
        val personalData = detailRepository.getPokemonPersonalData(pokemonNumber)

        localSpeciesNumber =
            if (localSpeciesNumber == 0) Uri.parse(personalData.species.url).lastPathSegment?.toInt()
                ?: 0 else speciesNumber

        // ポケモン特性を取得
        val species = detailRepository.getPokemonSpecies(localSpeciesNumber)
        return ApiResult(personalData = personalData, species = species)
    }

    /**
     * Stat更新
     */
    private fun updateCondition(
        roomResult: PokemonData? = null,
        apiResult: PokemonPersonalData,
        species: PokemonSpecies
    ) {
        // DBのデータが空だった場合はAPIから取得したデータをDBに保存
        if (roomResult == null) {
            _conditionState.update { currentState ->
                currentState.copy(
                    pokemonNumber = apiResult.id,
                    englishName = species.names.firstOrNull { names -> names.language.name == "en" }?.name
                        ?: "",
                    japaneseName = species.names.firstOrNull { names -> names.language.name == "ja" }?.name
                        ?: "",
                    description = species.flavorTextEntries.firstOrNull { flavorTextEntries ->
                        flavorTextEntries.language.name == "ja"
                    }?.flavorText ?: "日本語の説明が存在しません...",
                    imageUri = apiResult.sprites.other.officialArtwork.imgUrl ?: "",
                    speciesNumber = apiResult.id.toString(),
                    evolutionChainNumber = Uri.parse(species.evolutionChain.url).lastPathSegment
                        ?: ""
                )
            }
            // hp attack defense speed
            apiResult.stats.forEach { item ->
                _conditionState.update { currentState ->
                    when (item.stat.name) {
                        StatType.HP.type -> currentState.copy(hp = item.baseStat)
                        StatType.ATTACK.type -> currentState.copy(attack = item.baseStat)
                        StatType.DEFENSE.type -> currentState.copy(defense = item.baseStat)
                        StatType.SPEED.type -> currentState.copy(speed = item.baseStat)
                        else -> currentState
                    }
                }
            }
        } else {
            // ポケモン説明がない場合
            if (roomResult.description.isNullOrEmpty()) {
                _conditionState.update { currentState ->
                    currentState.copy(
                        description = species.flavorTextEntries.firstOrNull { flavorTextEntries ->
                            flavorTextEntries.language.name == "ja"
                        }?.flavorText ?: ""
                    )
                }
            } else {
                _conditionState.update { currentState ->
                    currentState.copy(
                        description = roomResult.description
                    )
                }
            }

            // 画像URLがない場合
            if (roomResult.imageUrl.isNullOrEmpty()) {
                _conditionState.update { currentState ->
                    currentState.copy(
                        imageUri = apiResult.sprites.other.officialArtwork.imgUrl ?: ""
                    )
                }
            } else {
                _conditionState.update { currentState ->
                    currentState.copy(
                        imageUri = roomResult.imageUrl
                    )
                }
            }

            // speciesNumberがない場合
            if (roomResult.speciesNumber.isNullOrEmpty()) {
                _conditionState.update { currentState ->
                    currentState.copy(
                        speciesNumber = Uri.parse(apiResult.species.url).lastPathSegment ?: ""
                    )
                }
            } else {
                _conditionState.update { currentState ->
                    currentState.copy(
                        speciesNumber = roomResult.speciesNumber
                    )
                }
            }

            // DB情報を使ってconditionStateを更新
            _conditionState.update { currentState ->
                currentState.copy(
                    pokemonNumber = roomResult.pokemonNumber,
                    englishName = roomResult.englishName ?: "",
                    japaneseName = roomResult.japaneseName,
                    hp = roomResult.hp ?: 0,
                    attack = roomResult.attack ?: 0,
                    defense = roomResult.defense ?: 0,
                    speed = roomResult.speed ?: 0,
                )
            }
        }
        // 分類、タイプ、高さ、重さを取得（DBに存在しない）
        _conditionState.update { currentState ->
            currentState.copy(
                genus = species.genera.firstOrNull { genera -> genera.language.name == "ja" }?.genus
                    ?: species.genera.firstOrNull { genera -> genera.language.name == "ja-Hrkt" }?.genus
                    ?: "該当する分類が存在しません...",
                type = apiResult.types.map { types -> types.type.name },
                height = apiResult.height / 10.0,
                weight = apiResult.weight / 10.0,
                evolutionChainNumber = Uri.parse(species.evolutionChain.url).lastPathSegment ?: ""
            )
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
                val searchId =
                    if (searchName.equals(
                            result.japaneseName,
                            ignoreCase = true
                        ) || searchName.equals(result.englishName, ignoreCase = true)
                    ) result.pokemonNumber else 0
                _pokemonListUiDataConditionState.update { currentState ->
                    currentState.copy(
                        pokemonNumber = searchId,
                        displayName = result.japaneseName,
                        imageUrl = result.imageUrl,
                    )
                }
            }
        }.onSuccess {
            getPokemonSpeciesById(
                pokemonNumber = _pokemonListUiDataConditionState.value.pokemonNumber,
            )
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
            send(PokemonDetailUiEvent.Error(it))
            _uiState.emit(PokemonDetailUiState.SearchError)
        }
    }

    /**
     * DB保存
     */
    private suspend fun saveDataBase(roomResult: PokemonData?, pokemonNumber: Int) {
        if (roomResult == null) {
            // 新規でDBに保存
            pokemonDataRepository.insertItem(
                listOf(
                    PokemonData(
                        pokemonNumber = pokemonNumber,
                        englishName = conditionState.value.englishName,
                        japaneseName = conditionState.value.japaneseName,
                        description = conditionState.value.description,
                        hp = conditionState.value.hp,
                        attack = conditionState.value.attack,
                        defense = conditionState.value.defense,
                        speed = conditionState.value.speed,
                        imageUrl = conditionState.value.imageUri,
                        speciesNumber = conditionState.value.speciesNumber,
                        type = conditionState.value.type,
                        genus = conditionState.value.genus,
                        evolutionChainNumber = conditionState.value.evolutionChainNumber
                    )
                )
            )
        } else {
            // pokemonNumberに該当するDBの情報を更新
            pokemonDataRepository.updatePokemonAllData(
                pokemonNumber = pokemonNumber,
                englishName = conditionState.value.englishName,
                japaneseName = conditionState.value.japaneseName,
                description = conditionState.value.description,
                hp = conditionState.value.hp,
                attack = conditionState.value.attack,
                defense = conditionState.value.defense,
                speed = conditionState.value.speed,
                imageUrl = conditionState.value.imageUri,
                speciesNumber = conditionState.value.speciesNumber,
                type = conditionState.value.type,
                genus = conditionState.value.genus,
                evolutionChainNumber = conditionState.value.evolutionChainNumber
            )
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
        }.onSuccess {
            _uiState.emit(
                PokemonDetailUiState.Fetched(
                    detailUiCondition = DetailUiCondition(
                        isLike = it != null && it.pokemonNumber == pokemonNumber
                    )
                )
            )
            _conditionState.update { currentState ->
                currentState.copy(
                    isLike = it != null && it.pokemonNumber == pokemonNumber
                )
            }
        }.onFailure {
            Log.d("error", "error：$it")
        }
    }
}

data class ApiResult(
    val personalData: PokemonPersonalData,
    val species: PokemonSpecies
)
