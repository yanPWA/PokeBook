package com.example.pokebook.data.pokemonData

import com.example.pokebook.data.pokemonData.PokemonData

/**
 * 与えられたデータソースから[Like]の挿入、更新、削除、取得を提供するリポジトリ
 */
interface PokemonDataRepository {
    /**
     * 指定されたデータ・ソースからすべての項目を取得
     */
    fun getAllItemsStream(): List<PokemonData>

    /**
     * データ・ソースに項目を挿入
     */
    suspend fun insertItem(pokemon: PokemonData)

    /**
     * データ・ソースから項目を削除
     */
    suspend fun deleteItem(pokemon: PokemonData)

    /**
     * データソースの項目を更新
     */
    suspend fun updateItem(pokemon: PokemonData)
}
