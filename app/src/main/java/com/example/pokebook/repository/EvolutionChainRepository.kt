package com.example.pokebook.repository

import com.example.pokebook.model.EvolutionChain
import com.example.pokebook.network.PokeApi
import retrofit2.http.Path

interface EvolutionChainRepository {
    suspend fun getPokemonEvolutionChain(@Path("path") evolutionNumber: String): EvolutionChain
}

class DefaultEvolutionChainRepository : EvolutionChainRepository {
    override suspend fun getPokemonEvolutionChain(evolutionNumber: String): EvolutionChain {
        return PokeApi.retrofitService.getPokemonEvolutionChain(
            evolutionNumber = evolutionNumber
        )
    }
}