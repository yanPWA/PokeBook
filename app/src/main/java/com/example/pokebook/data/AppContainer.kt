package com.example.pokebook.data

import android.content.Context
import com.example.pokebook.data.like.LikesRepository
import com.example.pokebook.data.like.OfflineLikesRepository
import com.example.pokebook.data.pokemonData.OfflinePokemonDataRepository
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.data.searchType.OfflineSearchTypeListRepository
import com.example.pokebook.data.searchType.SearchTypeListRepository
import com.example.pokebook.repository.DefaultHomeRepository
import com.example.pokebook.repository.DefaultPokemonDetailRepository
import com.example.pokebook.repository.DefaultSearchRepository
import com.example.pokebook.repository.HomeRepository
import com.example.pokebook.repository.PokemonDetailRepository
import com.example.pokebook.repository.ApiSearchRepository

/**
 * 依存性注入のためのアプリコンテナ
 */
interface AppContainer {
    val likesRepository: LikesRepository
    val pokemonDataRepository: PokemonDataRepository
    val searchRepository: ApiSearchRepository
    val pokemonDetailRepository: PokemonDetailRepository
    val homeRepository: HomeRepository
    val searchTypeListRepository: SearchTypeListRepository
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
    override val searchRepository: ApiSearchRepository by lazy {
        DefaultSearchRepository()
    }
    override val pokemonDetailRepository: PokemonDetailRepository by lazy {
        DefaultPokemonDetailRepository()
    }
    override val homeRepository: HomeRepository by lazy {
        DefaultHomeRepository()
    }
    override val searchTypeListRepository: SearchTypeListRepository by lazy {
        OfflineSearchTypeListRepository(PokemonDatabase.getDatabase(context).searchTypeListDao())
    }
}
