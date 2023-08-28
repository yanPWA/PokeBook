package com.example.pokebook.data.pokemonData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pokebook.data.pokemonData.PokemonData

@Dao
interface PokemonDataDao {
    //挿入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pokemon: PokemonData)

    // 同じ主キーを持つエンティティを更新
    @Update
    suspend fun update(pokemon: PokemonData)

    // 同じ主キーを持つエンティティを削除
    @Delete
    suspend fun delete(pokemon: PokemonData)

    @Query("SELECT * from pokemonData ORDER BY japaneseName ASC")
    fun getAllItems(): List<PokemonData>

    // japaneseName完全一致の検索
    @Query("SELECT * FROM pokemonData WHERE japaneseName = :keyword")
    fun searchByJapaneseName(keyword: String): PokemonData

    // 指定された範囲のデータの検索
    @Query("SELECT id,imageUrl,japaneseName FROM pokemonData WHERE id BETWEEN :startId AND :endId")
    fun getAllItemsBetweenIds(startId: Int, endId: Int): List<PokemonData>

    //　指定したIDのimageUrlにデータを挿入
    @Query("UPDATE pokemonData SET imageUrl = :imageUrl,speciesNumber = :speciesNumber WHERE id = :id")
    suspend fun updatePokemonData(id: Int, imageUrl: String, speciesNumber: String?)
}
