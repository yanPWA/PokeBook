package com.example.pokebook.data.pokemonData

import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataDao
import com.example.pokebook.data.pokemonData.PokemonDataRepository

class OfflinePokemonDataRepository(private val pokemonDataDao: PokemonDataDao):
    PokemonDataRepository {
    override fun getAllItemsStream(): List<PokemonData> = pokemonDataDao.getAllItems()

    override suspend fun insertItem(pokemon: PokemonData) = pokemonDataDao.insert(pokemon)

    override suspend fun deleteItem(pokemon: PokemonData) = pokemonDataDao.delete(pokemon)

    override suspend fun updateItem(pokemon: PokemonData) = pokemonDataDao.update(pokemon)
}