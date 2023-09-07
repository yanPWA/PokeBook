package com.example.pokebook.data.searchType

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchTypeListDao {
    //挿入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(searchTypeList: List<SearchTypeList>)

    // 同じ主キーを持つエンティティを更新
    @Update
    suspend fun update(searchType: SearchTypeList)

    // typeNumber完全一致の検索(全カラム取得)
    @Query("SELECT * FROM searchTypeList WHERE typeNumber = :typeNumber")
    fun searchByTypeNumber(typeNumber: Int): List<SearchTypeList>

    // typeNumber完全一致の検索(pokemonNumberカラム取得)
    @Query("SELECT pokemonNumber FROM searchTypeList WHERE typeNumber = :typeNumber")
    fun searchPokemonNumberByTypeNumber(typeNumber: Int): List<Int>

    // 該当するpokemonNumberのimageUrlとspeciesNumberを保存する
    @Query("UPDATE searchTypeList SET  imageUrl = :imageUrl, speciesNumber = :speciesNumber WHERE pokemonNumber = :pokemonNumber")
    suspend fun updateSpeciesNumberAndImageUrl(
        pokemonNumber: Int,
        imageUrl: String,
        speciesNumber: Int
    )

    // 該当するpokemonNumberのjapaneseNameを保存する
    @Query("UPDATE searchTypeList SET  japaneseName = :japaneseName WHERE pokemonNumber = :pokemonNumber")
    suspend fun updateJapaneseName(
        pokemonNumber: Int,
        japaneseName: String
    )
}