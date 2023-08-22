package com.example.pokebook.data.pokemonData

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 初回起動時にjsonファイルから取得するためのエンティティ
 * データベーステーブル
 */
@Entity(tableName = "pokemonData")
data class PokemonData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nameEnglish: String,
    val nameJapanese:String,
//    val type:List<String>
    val hp: Int,
    val Attack: Int,
    val Defense: Int,
    val Speed: Int
)
