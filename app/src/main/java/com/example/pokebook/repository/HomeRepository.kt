package com.example.pokebook.repository

import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.network.PokeApi
import retrofit2.http.Path

/**
 * 一覧画面取得に関するリポジトリー
 */
interface HomeRepository {
    suspend fun getPokemonList(): Pokemon

    suspend fun getPokemonList(offset: String): Pokemon

    suspend fun getPokemonPersonalData(number: Int): PokemonPersonalData
    suspend fun getPokemonSpecies(@Path("path") number: Int): PokemonSpecies
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

    override suspend fun getPokemonPersonalData(number: Int): PokemonPersonalData {
        return PokeApi.retrofitService.getPokemonPersonalData(number)
    }

    override suspend fun getPokemonSpecies(number: Int): PokemonSpecies {
        return PokeApi.retrofitService.getPokemonSpecies(number)
    }
}