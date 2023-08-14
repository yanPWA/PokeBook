package com.example.pokebook.data

import android.content.Context

/**
 * 依存性注入のためのアプリコンテナ
 */
interface AppContainer {
    val likesRepository: LikesRepository
}

/**
 * [OfflineLikesRepository]のインスタンスを提供する[AppContainer]の実装
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * [LikesRepository]の実装
     */
    override val likesRepository: LikesRepository by lazy {
        OfflineLikesRepository(PokemonDatabase.getDatabase(context).likeDao())
    }
}
