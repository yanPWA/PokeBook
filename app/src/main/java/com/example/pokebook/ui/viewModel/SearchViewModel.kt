package com.example.pokebook.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.repository.DefaultSearchRepository
import com.example.pokebook.ui.screen.TypeName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private var _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private val repository = DefaultSearchRepository()

    private var _conditionState: MutableStateFlow<SearchConditionState> =
        MutableStateFlow(SearchConditionState())
    val conditionState = _conditionState.asStateFlow()
    private val uiDataList = mutableListOf<PokemonListUiData>()

    /**
     * Type検索
     */
    fun getPokemonByType(typeNumber: String) = viewModelScope.launch {
        _uiState.emit(SearchUiState.Loading)
        runCatching {
            repository.getPokemonByType(typeNumber)
        }.onSuccess {
            uiDataList.clear()
            uiDataList += it.pokemon.map { item ->
                PokemonListUiData(
                    name = item.pokemonItem.name,
                    url = item.pokemonItem.url
                )
            }.toMutableList()
            // 一覧に表示する画像を取得
//            val imageUriList = it.pokemon.map { item ->
//                val pokemonNumber = Uri.parse(item.pokemonItem.url).lastPathSegment
//                Log.d("test","pokemonNumber：$pokemonNumber")
//                async {
//                    pokemonNumber?.let { number ->
//                        // ポケモンのパーソナル情報を取得
//                        repository.getPokemonPersonalData(number).apply {
//                            Log.d("test","getPokemonPersonalData：${this.sprites.other.officialArtwork.imgUrl}")
//                        }
//                    }
//                }
//            }.awaitAll()
//            Log.d("test","imageUriList：$imageUriList")
//            uiDataList.onEachIndexed { index, item ->
//                item.imageUri =
//                    imageUriList[index]?.sprites?.other?.officialArtwork?.imgUrl ?: ""
//            }
//            Log.d("test","画像URL取得されているはず：$uiDataList")
            //　一覧に表示するポケモンの日本語名を取得
//            val pokemonSpeciesList = it.pokemon.map { item ->
//                val pokemonNumber = Uri.parse(item.pokemonItem.url).lastPathSegment
//                async {
//                    pokemonNumber?.let { number ->
//                        repository.getPokemonSpecies(number).apply {
//                            Log.d("test","getPokemonSpecies：$this")
//                        }
//                    }
//                }
//            }.awaitAll()
//            Log.d("test","pokemonSpeciesList：$pokemonSpeciesList")
//            uiDataList.onEachIndexed { index, item ->
//                item.displayName =
//                    pokemonSpeciesList[index]?.names?.firstOrNull { name -> name.language.name == "ja" }?.name
//                        ?: ""
//                item.id = pokemonSpeciesList[index]?.id ?: 0
//            }
//            Log.d("test","名前・id取得されているはず：$uiDataList")
            _conditionState.update { currentState ->
                val typeName = when (typeNumber) {
                    TypeName.FIGHTING.number -> TypeName.FIGHTING.jaTypeName
                    TypeName.POISON.number -> TypeName.POISON.jaTypeName
                    TypeName.GROUND.number -> TypeName.GROUND.jaTypeName
                    TypeName.FLYING.number -> TypeName.FLYING.jaTypeName
                    TypeName.PSYCHIC.number -> TypeName.PSYCHIC.jaTypeName
                    TypeName.BUG.number -> TypeName.BUG.jaTypeName
                    TypeName.ROCK.number -> TypeName.ROCK.jaTypeName
                    TypeName.GHOST.number -> TypeName.GHOST.jaTypeName
                    TypeName.DRAGON.number -> TypeName.DRAGON.jaTypeName
                    TypeName.DARK.number -> TypeName.DARK.jaTypeName
                    TypeName.STEEL.number -> TypeName.STEEL.jaTypeName
                    TypeName.FAIRY.number -> TypeName.FAIRY.jaTypeName
                    TypeName.FIRE.number -> TypeName.FIRE.jaTypeName
                    TypeName.WATER.number -> TypeName.WATER.jaTypeName
                    TypeName.ELECTRIC.number -> TypeName.ELECTRIC.jaTypeName
                    TypeName.GRASS.number -> TypeName.GRASS.jaTypeName
                    TypeName.SHADOW.number -> TypeName.SHADOW.jaTypeName
                    TypeName.ICE.number -> TypeName.ICE.jaTypeName
                    TypeName.NORMAL.number -> TypeName.NORMAL.jaTypeName
                    TypeName.UNKNOWN.number -> TypeName.UNKNOWN.jaTypeName
                    else -> ""
                }
                currentState.copy(
                    pokemonTypeName = typeName
                )
            }
            Log.d("test", "uiDataList:$uiDataList")
            _uiState.emit(SearchUiState.Fetched(searchList = uiDataList))
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
        }
    }

    /**
     * 名前検索
     */
    fun getPokemonByName(name: String) = viewModelScope.launch {
        _uiState.emit(SearchUiState.Loading)
        runCatching {
            repository.getPokemonPersonalData(name)
        }.onSuccess {
            // TODO 返ってきたURLから個別情報取得する
        }.onFailure {
            Log.d("error", "e[getPokemonList]:$it")
        }
    }
}
