package com.example.pokebook.data.like

import com.example.pokebook.data.like.Like

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
     * データ・ソースの項目を更新
     */
    suspend fun updateItem(like: Like)

    /**
     * データ・ソースから項目をキーワードの項目を検索
     */
    suspend fun searchPokemonByName(pokemonNumber: Int):Like
}
