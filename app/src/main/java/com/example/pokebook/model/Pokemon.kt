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
//    val abilities: List<Ability>,
//    @SerialName("base_experience")
//    val baseExperience: Int,
//    val forms: List<Form>,
//    @SerialName("game_indices")
//    val gameIndices: List<GameIndex>,
//    val height: Int,
//    @SerialName("held_items")
//    val heldItems: List<Object>,
//    val id: Int,
//    @SerialName("is_default")
//    val isDefault: Boolean,
//    @SerialName("location_area_encounters")
//    val areaEncounters: String,
//    val moves: List<Moves>,
//    val name: String,
//    val order: Int,
//    @SerialName("past_types")
//    val pastTypes: List<Object>,
//    val species: Species,
    val sprites: Sprites,
//    val stats: List<Stats>,
//    val types: List<Type>,
//    val weight:Int
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
