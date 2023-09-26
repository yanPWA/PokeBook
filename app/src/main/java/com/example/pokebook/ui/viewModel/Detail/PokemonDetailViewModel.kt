package com.example.pokebook.ui.viewModel.Detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.like.LikesRepository
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.StatType
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

    private var _uiStateEvolution: MutableStateFlow<EvolutionChainUiState> =
        MutableStateFlow(EvolutionChainUiState.InitialState)
    val uiStateEvolution = _uiStateEvolution.asStateFlow()

    private var _conditionState: MutableStateFlow<PokemonDetailScreenUiData> =
        MutableStateFlow(PokemonDetailScreenUiData())
    val conditionState = _conditionState.asStateFlow()

    private var conditionStatePrevious = PreviousPokemonDetailScreenUiData()

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
        pokemonNumber: Int = 0,
        speciesNumber: Int = 0,
        englishName: String = ""
    ) = viewModelScope.launch {
        _uiState.emit(PokemonDetailUiState.Loading)
        runCatching {
            withContext(Dispatchers.IO) {
                // DBを検索
                val roomResult = if (englishName.isEmpty()) {
                    pokemonDataRepository.searchById(pokemonNumber)
                } else {
                    pokemonDataRepository.searchPokemonByKeyword(englishName)
                }

                // APIからポケモン情報取得
                val result = if (englishName.isEmpty()) {
                    getApiPokemonData(
                        pokemonNumber = pokemonNumber,
                        speciesNumber = speciesNumber
                    )
                } else {
                    getApiPokemonData(englishName = englishName)
                }

                // DBにデータが存在していなければAPIの情報で更新
                if (roomResult == null || roomResult.description.isNullOrEmpty()) {
                    // 必要情報を取得してconditionStateを更新
                    updateCondition(
                        apiResult = result.personalData,
                        species = result.species
                    )
                    // 新規でDBに保存
                    saveDataBase(
                        roomResult = roomResult,
                        pokemonNumber = result.species.id
                    )
                    // DBにdescriptionがない場合
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
                            height = result.personalData.height / 10.0,
                            weight = result.personalData.weight / 10.0,
                            speciesNumber = roomResult.speciesNumber ?: "",
                            evolutionChainNumber = roomResult.evolutionChainNumber ?: ""
                        )
                    }
                }
                //進化系譜取得
                getEvolutionChain()
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
        pokemonNumber: Int = 0,
        speciesNumber: Int = 0,
        englishName: String = ""
    ): ApiResult {
        var localSpeciesNumber = speciesNumber
        val personalData = if (englishName.isEmpty()) {
            detailRepository.getPokemonPersonalData(pokemonNumber)
        } else {
            detailRepository.getPokemonPersonalData(englishName)
        }

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
                val result = pokemonDataRepository.searchPokemonByKeyword(searchName)
                val searchId = if (searchName.equals(
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
     * 進化系譜取得
     */
    private fun getEvolutionChain() = viewModelScope.launch {
        // APIから進化系譜取得
        val resultApi = evolutionChainRepository.getPokemonEvolutionChain(
            conditionState.value.evolutionChainNumber
        ).convertToShowEvolution()

        // 日本語名取得後State更新
        getJapanesePokemonName(resultApi)
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
     * japanesePokemonNameを取得してconditionStateを更新
     */
    private fun getJapanesePokemonName(showEvolution: ShowEvolution) = viewModelScope.launch {
        var basePokemonJapaneseName = ""
        var basePokemonSpeciesNumber = ""
        var nextPokemonJapaneseName = ""
        var nextPokemonSpeciesNumber = ""
        var lastPokemonJapaneseName = ""
        var lastPokemonSpeciesNumber = ""
        val nextPokemonDataList = mutableListOf<NextPokemonData>()
        val lastPokemonDataList = mutableListOf<EvolutionPokemonDataState>()

        _uiStateEvolution.emit(EvolutionChainUiState.Loading)
        withContext(Dispatchers.IO) {
            // ベースポケモン
            if (!showEvolution.basePokemonSpeciesNumber.isNullOrEmpty() && !showEvolution.basePokemonName.isNullOrEmpty()) {
                //DB検索
                val result =
                    pokemonDataRepository.searchPokemonByKeyword(showEvolution.basePokemonName)
                basePokemonSpeciesNumber = showEvolution.basePokemonSpeciesNumber
                if (result == null) {
                    // APIから日本語名を取得
                    val species =
                        detailRepository.getPokemonSpecies(basePokemonSpeciesNumber.toInt())
                    basePokemonJapaneseName =
                        species.names.firstOrNull { name -> name.language.name == "ja" }?.name
                            ?: "NoName"
                    //DB 保存
                    saveJapanesePokemonName(
                        pokemonNumber = species.id,
                        japaneseName = basePokemonJapaneseName,
                        speciesNumber = basePokemonSpeciesNumber
                    )
                } else {
                    basePokemonJapaneseName = result.japaneseName
                }
            }

            // 進化ポケモン
            showEvolution.evolution?.map { parent ->
                if (!parent.nextPokemonName.isNullOrEmpty() && !parent.nextPokemonSpeciesNumber.isNullOrEmpty()) {
                    //DB検索
                    val result =
                        pokemonDataRepository.searchPokemonByKeyword(parent.nextPokemonName)
                    nextPokemonSpeciesNumber = parent.nextPokemonSpeciesNumber

                    if (result == null) {
                        // APIから日本語名を取得
                        val species =
                            detailRepository.getPokemonSpecies(nextPokemonSpeciesNumber.toInt())
                        nextPokemonJapaneseName =
                            species.names.firstOrNull { name -> name.language.name == "ja" }?.name
                                ?: "NoName"
                        //DB 保存
                        saveJapanesePokemonName(
                            pokemonNumber = species.id,
                            japaneseName = nextPokemonJapaneseName,
                            speciesNumber = nextPokemonSpeciesNumber
                        )
                    } else {
                        nextPokemonJapaneseName = result.japaneseName
                    }
                }

                // 最終進化ポケモンListをクリアする
                lastPokemonDataList.clear()
                parent.lastPokemonName?.mapIndexed { index, name ->
                    if (name.isNotEmpty()) {
                        if (parent.lastPokemonSpeciesNumber == null) return@withContext
                        //DB検索
                        val roomResult =
                            pokemonDataRepository.searchPokemonByKeyword(name)
                        if (roomResult == null && parent.lastPokemonSpeciesNumber.size >= index.plus(
                                1
                            )
                        ) {
                            lastPokemonSpeciesNumber = parent.lastPokemonSpeciesNumber[index]
                            // APIから日本語名を取得
                            val species =
                                detailRepository.getPokemonSpecies(lastPokemonSpeciesNumber.toInt())
                            lastPokemonJapaneseName =
                                species.names.firstOrNull { speciesName -> speciesName.language.name == "ja" }?.name
                                    ?: "NoName"
                            //DB 保存
                            saveJapanesePokemonName(
                                pokemonNumber = species.id,
                                japaneseName = lastPokemonJapaneseName,
                                speciesNumber = lastPokemonSpeciesNumber
                            )
                        } else if (roomResult.speciesNumber.isNullOrEmpty() && parent.lastPokemonSpeciesNumber.size >= index.plus(
                                1
                            )
                        ) {
                            lastPokemonSpeciesNumber = parent.lastPokemonSpeciesNumber[index]
                            // APIから日本語名を取得
                            val species =
                                detailRepository.getPokemonSpecies(lastPokemonSpeciesNumber.toInt())
                            lastPokemonJapaneseName =
                                species.names.firstOrNull { speciesName -> speciesName.language.name == "ja" }?.name
                                    ?: "NoName"

                            //DB 保存
                            saveJapanesePokemonName(
                                pokemonNumber = species.id,
                                japaneseName = lastPokemonJapaneseName,
                                speciesNumber = lastPokemonSpeciesNumber
                            )
                        } else {
                            lastPokemonJapaneseName = roomResult.japaneseName
                            lastPokemonSpeciesNumber = roomResult.speciesNumber ?: ""
                        }
                        // 進化ポケモンが所持する最終進化ポケモンList
                        lastPokemonDataList += EvolutionPokemonDataState(
                            japaneseName = lastPokemonJapaneseName,
                            speciesNumber = lastPokemonSpeciesNumber,
                            englishName = name
                        )
                    }
                }
                nextPokemonDataList.add(
                    NextPokemonData(
                        nextPokemonData = EvolutionPokemonDataState(
                            japaneseName = nextPokemonJapaneseName,
                            speciesNumber = nextPokemonSpeciesNumber,
                            englishName = parent.nextPokemonName
                        ),
                        lastPokemonData = lastPokemonDataList.toMutableList()
                    )
                )
            }
            _conditionState.update { currentState ->
                currentState.copy(
                    displayEvolution = DisplayEvolution(
                        basePokemonData = EvolutionPokemonDataState(
                            japaneseName = basePokemonJapaneseName,
                            speciesNumber = basePokemonSpeciesNumber,
                            englishName = showEvolution.basePokemonName
                        ),
                        nextPokemonData = nextPokemonDataList
                    )
                )
            }
        }
        _uiStateEvolution.emit(EvolutionChainUiState.Fetched)
    }

    /**
     * 取得したjapanesePokemonNameとspeciesNumberをDBに保存する
     */
    private fun saveJapanesePokemonName(
        pokemonNumber: Int,
        japaneseName: String,
        speciesNumber: String
    ) = viewModelScope.launch {
        pokemonDataRepository.updatePokemonAllData(
            pokemonNumber = pokemonNumber,
            japaneseName = japaneseName,
            speciesNumber = speciesNumber
        )
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

    /**
     * 別の詳細画面遷移前に詳細画面情報を格納
     */
    fun saveConditionState() {
        conditionStatePrevious = PreviousPokemonDetailScreenUiData(
            pokemonNumber = conditionState.value.pokemonNumber,
            englishName = conditionState.value.englishName,
            japaneseName = conditionState.value.japaneseName,
            description = conditionState.value.description,
            genus = conditionState.value.genus,
            type = conditionState.value.type,
            hp = conditionState.value.hp,
            attack = conditionState.value.attack,
            defense = conditionState.value.defense,
            speed = conditionState.value.speed,
            imageUri = conditionState.value.imageUri,
            height = conditionState.value.height,
            weight = conditionState.value.weight,
            isLike = conditionState.value.isLike,
            speciesNumber = conditionState.value.speciesNumber,
            evolutionChainNumber = conditionState.value.evolutionChainNumber,
            displayEvolution = conditionState.value.displayEvolution
        )
    }


    /**
     * 別の詳細画面から戻る時に表示する詳細画面
     */
    fun onClickBackButton() {
        _conditionState.update { currentState ->
            currentState.copy(
                pokemonNumber = conditionStatePrevious.pokemonNumber,
                englishName = conditionStatePrevious.englishName,
                japaneseName = conditionStatePrevious.japaneseName,
                description = conditionStatePrevious.description,
                genus = conditionStatePrevious.genus,
                type = conditionStatePrevious.type,
                hp = conditionStatePrevious.hp,
                attack = conditionStatePrevious.attack,
                defense = conditionStatePrevious.defense,
                speed = conditionStatePrevious.speed,
                imageUri = conditionStatePrevious.imageUri,
                height = conditionStatePrevious.height,
                weight = conditionStatePrevious.weight,
                isLike = conditionStatePrevious.isLike,
                speciesNumber = conditionStatePrevious.speciesNumber,
                evolutionChainNumber = conditionStatePrevious.evolutionChainNumber,
                displayEvolution = conditionStatePrevious.displayEvolution
            )
        }
    }
}

data class ApiResult(
    val personalData: PokemonPersonalData,
    val species: PokemonSpecies
)
