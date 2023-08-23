package com.example.pokebook.data.like

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pokebook.data.like.Like
import com.example.pokebook.data.pokemonData.PokemonData

@Dao
interface LikeDao {
    // 挿入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: Like)

    // 同じ主キーを持つエンティティを更新
    @Update
    suspend fun update(like: Like)

    // 同じ主キーを持つエンティティを削除
    @Delete
    suspend fun delete(like: Like)

    @Query("SELECT * from likes ORDER BY name ASC")
    fun getAllItems(): List<Like>

    @Query("SELECT * FROM likes WHERE pokemonNumber = :pokemonNumber")
    fun searchByName(pokemonNumber: Int): Like
}
