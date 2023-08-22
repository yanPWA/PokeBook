package com.example.pokebook.json

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.pokebook.data.PokemonDatabase
import com.example.pokebook.data.pokemonData.PokemonData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

private val json = Json {
    ignoreUnknownKeys = true
}

fun readJson(context: Context) {
    //jsonファイルの読み込み(テキスト形式)
    val assetsManager = context.resources.assets
    val inputStream = assetsManager.open("pokemonData.json")
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val jsonStr = bufferedReader.readText()
    val pokemonList = json.decodeFromString<List<PokemonDataByJson>>(jsonStr)

    //Roomに書き込む
    insertPokemonDataToDB(
        context = context,
        pokemonData = pokemonList
    )
}

// JSONデータからデシリアライズしたPokemonDataByJsonオブジェクトを元に、
// PokemonDataのインスタンスを作成してデータベースに挿入する
fun insertPokemonDataToDB(context: Context, pokemonData: List<PokemonDataByJson>) {
    val database = Room.databaseBuilder(
        context.applicationContext,
        PokemonDatabase::class.java,
        "pokemon-database"
    ).build()

    val pokemonDataDao = database.pokemonDataDao()

    // デシリアライズしたポケモンデータをPokemonDataオブジェクトに変換してデータベースに挿入
    for (pokemon in pokemonData) {
        CoroutineScope(Dispatchers.IO).launch {
            pokemonDataDao.insert(pokemon.toPokemonData())
        }
    }
}
