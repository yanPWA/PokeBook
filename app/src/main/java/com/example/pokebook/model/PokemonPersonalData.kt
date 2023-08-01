package com.example.pokebook.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ポケモン個体の情報
 * https://pokeapi.co/api/v2/pokemon/1/
 */
@Serializable
data class PokemonPersonalData(
    val species: Species,
    val sprites: Sprites,
    val stats: List<Stats>,
    val types: List<Types>,
    val height:Int,
    val weight: Int
)

@Serializable
data class Sprites(
    val other: Other
)

@Serializable
data class Other(
    @SerialName("official-artwork")
    val officialArtwork: OfficialArtwork
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    val imgUrl: String?
)

@Serializable
data class Species(
    val name: String,
    val url: String?  // ※1
)

@Serializable
data class Stats(
    @SerialName("base_stat")
    val baseStat:Int,
    val stat: Stat
)

/**
 * name:hp, attack, defense, speed
 */
@Serializable
data class Stat(
    val name: String
)

@Serializable
data class Types(
    val type: Type
)

@Serializable
data class Type(
    val name: String
)

/**
 * ポケモンの種類に関する情報
 * https://pokeapi.co/api/v2/pokemon-species/1/　※1
 */
@Serializable
data class PokemonSpecies(
    /** ポケモン日本語名 */
    val names: List<Names>,
    /** ポケモンの説明 */
    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntries>,
    /** ポケモンID */
    val id: Int,
    /** 属性 */
    val genera: List<Genera>
)

@Serializable
data class Names(
    val name: String,
    val language: Language
)

@Serializable
data class Language(
    val name: String, //jaが対象
    val url: String
)

@Serializable
data class FlavorTextEntries(
    @SerialName("flavor_text")
    val flavorText: String,
    val language: Language
)

@Serializable
data class Genera(
    val genus: String,
    val language: Language
)

// TODO これを使いたい
enum class StatType(val type: String){
    HP("hp"),
    ATTACK("attack"),
    DEFENSE("defense"),
    SPEED("speed")
}