package com.example.pokebook.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * タイプ別に検索した結果のポケモンを格納する場所
 */
@Serializable
data class PokemonTypeSearchResult(
    val pokemon: List<PokemonItem>
)

@Serializable
data class PokemonItem(
    @SerialName("pokemon")
    val pokemonItem:PokemonListItem
)


