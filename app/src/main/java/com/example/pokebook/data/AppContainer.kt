package com.example.pokebook.data

import android.content.Context
import com.example.pokebook.data.like.LikesRepository
import com.example.pokebook.data.like.OfflineLikesRepository
import com.example.pokebook.data.pokemonData.OfflinePokemonDataRepository
import com.example.pokebook.data.pokemonData.PokemonDataRepository

/**
 * 依存性注入のためのアプリコンテナ
 */
interface AppContainer {
    val likesRepository: LikesRepository
    val pokemonDataRepository: PokemonDataRepository
}

/**
 * 各Repositoryのインスタンスを提供する[AppContainer]の実装
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * [LikesRepository]の実装
     */
    override val likesRepository: LikesRepository by lazy {
        OfflineLikesRepository(PokemonDatabase.getDatabase(context).likeDao())
    }
    override val pokemonDataRepository: PokemonDataRepository by lazy {
        OfflinePokemonDataRepository(PokemonDatabase.getDatabase(context).pokemonDataDao())
    }
}
