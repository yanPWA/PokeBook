package com.example.pokebook.json

import com.example.pokebook.data.pokemonData.PokemonData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Jsonデータの受け皿
 */
@Serializable
data class PokemonDataByJson(
    @SerialName("id")
    val pokemonNumber: Int,
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
