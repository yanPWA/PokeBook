package com.example.pokebook.repository

import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.network.PokeApi

/**
 * 一覧画面取得に関するリポジトリー
 */
interface HomeRepository {
    suspend fun getPokemonList(): Pokemon

    suspend fun getPokemonList(offset: String): Pokemon

    suspend fun getPokemonPersonalData(number: String): PokemonPersonalData
}

class DefaultHomeRepository : HomeRepository {
    override suspend fun getPokemonList(): Pokemon {
        return PokeApi.retrofitService.getPokemonList()
    }

    override suspend fun getPokemonList(offset: String): Pokemon {
        return PokeApi.retrofitService.getPokemonList(
            offset = offset,
            limit = "20"
        )
    }

    override suspend fun getPokemonPersonalData(number: String): PokemonPersonalData {
        return PokeApi.retrofitService.getPokemonPersonalData(number)
    }
}