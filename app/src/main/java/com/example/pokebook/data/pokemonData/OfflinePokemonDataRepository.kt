package com.example.pokebook.data.pokemonData

import android.util.Log
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataDao
import com.example.pokebook.data.pokemonData.PokemonDataRepository

class OfflinePokemonDataRepository(private val pokemonDataDao: PokemonDataDao) :
    PokemonDataRepository {
    override fun getAllItemsStream(): List<PokemonData> = pokemonDataDao.getAllItems()

    override suspend fun insertItem(pokemonList: List<PokemonData>) =
        pokemonDataDao.insert(pokemonList)

    override suspend fun deleteItem(pokemon: PokemonData) = pokemonDataDao.delete(pokemon)

    override suspend fun updateItem(pokemon: PokemonData) = pokemonDataDao.update(pokemon)
    override suspend fun searchPokemonByKeyword(keyword: String): PokemonData =
        pokemonDataDao.searchByJapaneseName(keyword)

    override suspend fun getAllItemsBetweenIds(startId: Int, endId: Int): List<PokemonData> =
        pokemonDataDao.getAllItemsBetweenIds(startId, endId)

    override suspend fun updatePokemonData(
        id: Int,
        imageUrl: String,
        speciesNumber: String?
    ) = pokemonDataDao.updatePokemonData(
        id = id,
        imageUrl = imageUrl,
        speciesNumber = speciesNumber
    )

    override suspend fun updatePokemonAllData(
        id: Int?,
        pokemonNumber: Int?,
        englishName: String?,
        japaneseName: String?,
        description: String?,
        hp: Int?,
        attack: Int?,
        defense: Int?,
        speed: Int?,
        imageUrl: String?,
        genus: String?,
        type: List<String>?,
        speciesNumber: String?
    ) = pokemonDataDao.updatePokemonAllData(
        pokemonNumber = pokemonNumber,
        englishName = englishName,
        japaneseName = japaneseName,
        description = description,
        hp = hp,
        attack = attack,
        defense = defense,
        speed = speed,
        imageUrl = imageUrl,
        genus = genus,
        type = type,
        speciesNumber = speciesNumber
    )

    override suspend fun searchById(id: Int): PokemonData = pokemonDataDao.searchById(id)
}
