package com.example.pokebook.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val count: Int,
    val next: String,
    val previous: String? = null,
    val results: List<PokemonListItem>
)

/**
 * ポケモン一覧取得
 */
@Serializable
data class PokemonListItem(
    val name: String = "",
    val url: String = ""
)


/**
 * ポケモン個体の情報
 */
@Serializable
data class PokemonPersonalData(
    val sprites: Sprites,
)
@Serializable
data class Sprites(
    val other: Other
)

@Serializable
data class Other(
    @SerialName("official-artwork")
    val officialArtwork:OfficialArtwork
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    val imgUrl:String
)

@Serializable
data class Profile(
    val number: Int = 0,
    val name: String = "",
    val type: String = "",
    val attribution: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val height: Int = 0,
    val weight: Int = 0
)

@Serializable
data class Performance(
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0
)
