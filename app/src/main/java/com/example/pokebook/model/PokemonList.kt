package com.example.pokebook.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ポケモン一覧情報
 */
@Serializable
data class Pokemon(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonListItem>
)

/**
 * 各ポケモンのURL
 */
@Serializable
data class PokemonListItem(
    val name: String = "",
    val url: String = ""
)


