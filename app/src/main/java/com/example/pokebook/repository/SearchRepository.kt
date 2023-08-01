package com.example.pokebook.repository

import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.PokemonTypeSearchResult
import com.example.pokebook.network.PokeApi
import retrofit2.http.Path

interface SearchRepository {
    suspend fun getPokemonPersonalData(pokemon: String): PokemonPersonalData
    suspend fun getPokemonByType(@Path("path") typeNumber: String): PokemonTypeSearchResult
    suspend fun getPokemonSpecies(@Path("path") number: String): PokemonSpecies
}

class DefaultSearchRepository : SearchRepository {
    override suspend fun getPokemonPersonalData(pokemon: String): PokemonPersonalData {
        return PokeApi.retrofitService.getPokemonPersonalData(pokemon)
    }

    override suspend fun getPokemonByType(typeNumber: String): PokemonTypeSearchResult {
       return PokeApi.retrofitService.getPokemonByType(typeNumber)
    }

    override suspend fun getPokemonSpecies(number: String): PokemonSpecies {
        return PokeApi.retrofitService.getPokemonSpecies(number)
    }
}