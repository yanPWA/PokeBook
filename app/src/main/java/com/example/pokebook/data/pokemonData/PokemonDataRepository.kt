package com.example.pokebook.data.pokemonData

import com.example.pokebook.data.pokemonData.PokemonData

/**
 * Jsonデータを元に作成されたDBに対して操作を行うRepository
 */
interface PokemonDataRepository {
    /**
     * 指定されたデータ・ソースからすべての項目を取得
     */
    fun getAllItemsStream(): List<PokemonData>

    /**
     * データ・ソースに項目を挿入
     */
    suspend fun insertItem(pokemonList: List<PokemonData>)

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
    suspend fun updatePokemonData(
        id: Int,
        imageUrl: String,
        speciesNumber: String? = null
    )

    /**
     * 指定したIDの必要なカラムを更新
     */
    suspend fun updatePokemonAllData(
        id: Int?=null,
        pokemonNumber:Int? = null,
        englishName: String? = null,
        japaneseName: String? = null,
        description: String? = null,
        hp: Int? = null,
        attack: Int? = null,
        defense: Int? = null,
        speed: Int? = null,
        imageUrl: String? =null,
        genus: String? = null,
        type: List<String>? = null,
        speciesNumber: String? = null
    )


    /**
     * id完全一致の検索
     */
    suspend fun searchById(id: Int): PokemonData
}
