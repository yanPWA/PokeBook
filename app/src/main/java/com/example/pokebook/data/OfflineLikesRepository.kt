package com.example.pokebook.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class OfflineLikesRepository(private val likeDao: LikeDao) : LikesRepository {
    override fun getAllItemsStream(): List<Like> = likeDao.getAllItems()

    override suspend fun insertItem(like: Like) = likeDao.insert(like)

    override suspend fun deleteItem(like: Like) = likeDao.delete(like)

    override suspend fun updateItem(like: Like) = likeDao.update(like)
}
