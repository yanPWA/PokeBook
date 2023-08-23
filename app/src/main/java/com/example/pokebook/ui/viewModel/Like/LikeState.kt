package com.example.pokebook.ui.viewModel.Like

import com.example.pokebook.data.like.Like
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailScreenUiData
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import java.util.UUID

/**
 * Like画面の状態に関する情報
 */
sealed class LikeUiState {
    object Loading : LikeUiState()
    data class Fetched(
        val uiDataList: MutableList<LikeDetails>
    ) : LikeUiState()

    object InitialState : LikeUiState()
    object ResultError : LikeUiState()
}

data class LikeScreenConditionState(
    val isScrollTop: Boolean = true
)

/**
 * 一つのLikeを表す
 */
data class LikeDetails(
    val pokemonNumber: Int = 0,
    val name: String = "",
    val displayName: String = "",
    val description: String = "",
    val genus: String = "",
    val type: List<String> = emptyList(),
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val speed: Int = 0,
    val imageUrl: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    var isLike: Boolean = true
)

/**
 * エラー表示に関する情報
 */
sealed class LikeUiEvent(
    // ワンショットのイベントとして管理
    val id: Long = UUID.randomUUID().mostSignificantBits
) {
    data class Error(val e: Throwable) : LikeUiEvent()
}

/**
 * LikeDetails -> Like
 */
fun LikeDetails.toLike(): Like = Like(
    pokemonNumber = pokemonNumber,
    name = name,
    displayName = displayName,
    imageUrl = imageUrl,
)


/**
 * PokemonDetailScreenUiData -> LikeDetails
 */
fun PokemonDetailScreenUiData.toLikeDetails(): LikeDetails = LikeDetails(
    pokemonNumber = this.pokemonNumber,
    name = this.name,
    displayName = this.name,
    description = this.description,
    genus = this.genus,
    type = this.type,
    hp = this.hp,
    attack = this.attack,
    defense = this.defense,
    speed = this.speed,
    imageUrl = this.imageUri,
    height = this.height,
    weight = this.weight,
    isLike = this.isLike
)

/**
 * LikeDetails -> PokemonListUiData
 */
fun LikeDetails.toPokemonListUiDataByLikeDetails(): PokemonListUiData = PokemonListUiData(
    pokemonNumber = pokemonNumber,
    name = name,
    displayName = displayName,
    imageUrl = imageUrl
)

/**
 * List<Like> -> MutableList<PokemonListUiData>
 */
fun List<Like>.toPokemonListUiDataList(): MutableList<LikeDetails> {
    val convertList = mutableListOf<LikeDetails>()

    convertList.addAll(
        this.map { like ->
            LikeDetails(
                pokemonNumber = like.pokemonNumber,
                name = like.name,
                displayName = like.displayName,
                imageUrl = like.imageUrl,
                isLike = true
            )
        }
    )
    return convertList
}
