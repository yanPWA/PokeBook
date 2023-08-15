package com.example.pokebook.data

import kotlinx.coroutines.flow.Flow

/**
 * 与えられたデータソースから[Like]の挿入、更新、削除、取得を提供するリポジトリ
 */
interface LikesRepository {
    /**
     * 指定されたデータ・ソースからすべての項目を取得
     */
    fun getAllItemsStream(): List<Like>

    /**
     * データ・ソースに項目を挿入
     */
    suspend fun insertItem(like: Like)

    /**
     * データ・ソースから項目を削除
     */
    suspend fun deleteItem(like: Like)

    /**
     * データソースの項目を更新
     */
    suspend fun updateItem(like: Like)
}
