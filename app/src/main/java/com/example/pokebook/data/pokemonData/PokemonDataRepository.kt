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

    /**
     * 指定されたjapaneseNameを検索
     */
    suspend fun searchPokemonByKeyword(keyword: String): PokemonData

    /**
     * 指定された範囲のデータの検索
     */
    suspend fun getAllItemsBetweenIds(startId: Int, endId: Int): List<PokemonData>

    /**
     * 指定したIDのimageUrl、speciesNumberにデータを挿入
     */
    suspend fun updatePokemonData(id: Int, imageUrl: String, speciesNumber: String?)
}
