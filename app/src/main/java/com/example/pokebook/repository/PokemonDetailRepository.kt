package com.example.pokebook.repository

import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.network.PokeApi
import retrofit2.http.Path

interface PokemonDetailRepository {
    suspend fun getPokemonPersonalData(@Path("path") number: Int): PokemonPersonalData
    suspend fun getPokemonSpecies(@Path("path") number: Int): PokemonSpecies
}

class DefaultPokemonDetailRepository: PokemonDetailRepository{
    override suspend fun getPokemonPersonalData(number: Int): PokemonPersonalData {
        return PokeApi.retrofitService.getPokemonPersonalData(number)
    }

    override suspend fun getPokemonSpecies(number: Int): PokemonSpecies {
        return PokeApi.retrofitService.getPokemonSpecies(number)
    }
}