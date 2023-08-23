package com.example.pokebook.data.like

import com.example.pokebook.data.like.Like
import com.example.pokebook.data.like.LikeDao
import com.example.pokebook.data.like.LikesRepository

class OfflineLikesRepository(private val likeDao: LikeDao) : LikesRepository {
    override fun getAllItemsStream(): List<Like> = likeDao.getAllItems()

    override suspend fun insertItem(like: Like) = likeDao.insert(like)

    override suspend fun deleteItem(like: Like) = likeDao.delete(like)

    override suspend fun updateItem(like: Like) = likeDao.update(like)
    override suspend fun searchPokemonByName(pokemonNumber: Int): Like  = likeDao.searchByName(pokemonNumber)
}
