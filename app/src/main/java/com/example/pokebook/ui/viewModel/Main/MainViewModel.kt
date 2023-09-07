package com.example.pokebook.ui.viewModel.Main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.PokemonDatabase
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
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class MainViewModel(
    private val pokemonDataRepository: PokemonDataRepository
) : ViewModel() {
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
        }.onSuccess {
            // 次のメソッドに移行するため、何もしない
        }.onFailure {
           //TODO いずれエラー処理を実装
        }
    }

    /**
     * デシリアライズしたポケモンデータをPokemonDataオブジェクトに変換してデータベースに挿入
     */
    private suspend fun convertAndWriteToDB(pokemonList: List<PokemonDataByJson>) {
        val pokemonDataList = pokemonList.map {
            PokemonData(
                pokemonNumber = it.pokemonNumber,
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
