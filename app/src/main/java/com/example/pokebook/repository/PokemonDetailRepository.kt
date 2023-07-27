package com.example.pokebook.repository

import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.network.PokeApi
import retrofit2.http.Path

interface PokemonDetailRepository {
    suspend fun getPokemonPersonalData(@Path("path") number: String): PokemonPersonalData
    suspend fun getPokemonSpecies(@Path("path") number: String): PokemonSpecies
}

class DefaultPokemonDetailRepository: PokemonDetailRepository{
    override suspend fun getPokemonPersonalData(number: String): PokemonPersonalData {
        return PokeApi.retrofitService.getPokemonPersonalData(number)
    }

    override suspend fun getPokemonSpecies(number: String): PokemonSpecies {
        return PokeApi.retrofitService.getPokemonSpecies(number)
    }
}