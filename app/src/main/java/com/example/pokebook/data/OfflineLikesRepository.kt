package com.example.pokebook.data

import kotlinx.coroutines.flow.Flow

class OfflineLikesRepository(private val likeDao: LikeDao) : LikesRepository {
    override  fun getAllItemsStream(): Flow<List<Like>> =likeDao.getAllItems()

    override suspend fun insertItem(like: Like)  = likeDao.insert(like)

    override suspend fun deleteItem(like: Like) = likeDao.delete(like)

    override suspend fun updateItem(like: Like) = likeDao.update(like)
}
