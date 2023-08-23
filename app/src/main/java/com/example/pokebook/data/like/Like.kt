package com.example.pokebook.data.like

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * お気に入りポケモンをDBに保存するためのエンティティ
 * データベーステーブル
 */
@Entity(tableName = "likes")
data class Like(
    val id: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val pokemonNumber:Int = 0,
    val name: String,
    val displayName: String,
    val imageUrl: String,
)
