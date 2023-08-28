package com.example.pokebook.data.pokemonData

import android.util.Log
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataDao
import com.example.pokebook.data.pokemonData.PokemonDataRepository

class OfflinePokemonDataRepository(private val pokemonDataDao: PokemonDataDao) :
    PokemonDataRepository {
    override fun getAllItemsStream(): List<PokemonData> = pokemonDataDao.getAllItems()

    override suspend fun insertItem(pokemon: PokemonData) = pokemonDataDao.insert(pokemon)

    override suspend fun deleteItem(pokemon: PokemonData) = pokemonDataDao.delete(pokemon)

    override suspend fun updateItem(pokemon: PokemonData) = pokemonDataDao.update(pokemon)
    override suspend fun searchPokemonByKeyword(keyword: String): PokemonData =
        pokemonDataDao.searchByJapaneseName(keyword)

    override suspend fun getAllItemsBetweenIds(startId: Int, endId: Int): List<PokemonData> =
        pokemonDataDao.getAllItemsBetweenIds(startId, endId).apply {
            Log.d("test","startId:$startId,endId:$endId")
        }

    override suspend fun updatePokemonData(id: Int, imageUrl: String,speciesNumber:String?) {
        pokemonDataDao.updatePokemonData(id, imageUrl,speciesNumber)
    }
}
