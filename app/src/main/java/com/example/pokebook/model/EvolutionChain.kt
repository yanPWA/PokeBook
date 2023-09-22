package com.example.pokebook.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 進化系の情報を取得
 */
@Serializable
data class EvolutionChain(
    val chain: Chain? =null
)

@Serializable
data class Chain(
    @SerialName("evolves_to")
    val evolves: List<Evolves>?,
    @SerialName("species")
    val basePokemon: EvolvesSpecies?
)

@Serializable
data class Evolves(
    @SerialName("evolves_to")
    val evolves: List<NextEvolves>?,
    @SerialName("species")
    val nextGeneration: EvolvesSpecies?
)

@Serializable
data class NextEvolves(
    @SerialName("species")
    val lastGeneration: EvolvesSpecies
)

@Serializable
data class EvolvesSpecies(
    val name: String?,
    val url: String?
)