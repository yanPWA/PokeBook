package com.example.pokebook.ui.viewModel.Main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.pokemonData.pokemonDataByJson
import com.example.pokebook.json.PokemonDataByJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainViewModel(
    private val pokemonDataRepository: PokemonDataRepository
) : ViewModel() {
    //    private val _isDataReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
//    val isDataReady = _isDataReady.asStateFlow()
// TODO LiveDataとFlowどちらが適切なのか？
    private val _isReady: MutableLiveData<Boolean> = MutableLiveData(false)
    val isReady: LiveData<Boolean> get() = _isReady

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * JsonデータをDBに書き込む
     */
    fun readJson() = viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                // jsonデータをデシリアライズ
                val pokemonList = json.decodeFromString<List<PokemonDataByJson>>(pokemonDataByJson)
                // DB書き込み
                convertAndWriteToDB(pokemonList)
            }
            delay(2000) // DB書き込みがない場合スプラッシュが一瞬で消えるため、デフォルト２秒表示させる
        }.onSuccess {
//            _isDataReady.emit(true)
            _isReady.postValue(true)
        }.onFailure {
//            _isDataReady.emit(false)
            _isReady.postValue(false)
        }
    }

    /**
     * デシリアライズしたポケモンデータをPokemonDataオブジェクトに変換してデータベースに挿入
     */
    private suspend fun convertAndWriteToDB(pokemonList: List<PokemonDataByJson>) {
        val pokemonDataList = pokemonList.map {
            PokemonData(
                id = it.id,
                englishName = it.name.english,
                japaneseName = it.name.japanese,
                hp = it.base.hp,
                attack = it.base.attack,
                defense = it.base.defense,
                speed = it.base.speed,
            )
        }
        pokemonDataRepository.insertItem(pokemonDataList)
    }
}
