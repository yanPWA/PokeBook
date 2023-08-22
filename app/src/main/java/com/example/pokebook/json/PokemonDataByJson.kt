package com.example.pokebook.json

import androidx.room.PrimaryKey
import com.example.pokebook.data.pokemonData.PokemonData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Jsonデータの受け皿
 */
@Serializable
data class PokemonDataByJson(
    val id: Int,
    val name: Name,
    val type: List<String>,
    val base: Base
)

@Serializable
data class Name(
    val english: String,
    val japanese: String
)

@Serializable
data class Base(
    @SerialName("HP")
    val hp: Int,
    @SerialName("Attack")
    val attack: Int,
    @SerialName("Defense")
    val defense: Int,
    @SerialName("Speed")
    val speed: Int
)

/**
 * PokemonDataByJson -> PokemonData
 */
fun PokemonDataByJson.toPokemonData(): PokemonData = PokemonData(
    id = this.id,
    nameEnglish = this.name.english,
    nameJapanese = this.name.japanese,
//    typy = this.type,
    hp = this.base.hp,
    Attack = this.base.attack,
    Defense = this.base.defense,
    Speed = this.base.speed
)
