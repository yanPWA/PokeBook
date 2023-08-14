package com.example.pokebook.ui.viewModel.Like

import com.example.pokebook.data.Like

/**
 * Like画面の状態に関する情報
 */
sealed class LikeUiState {
    object Loading : LikeUiState()
    data class Fetched(
        val uiDataList: MutableList<PokemonListUiData>
    ) : LikeUiState()

    object InitialState : LikeUiState()
    object ResultError: LikeUiState()
}

/**
 *　Like表示用データ
 *　 - id
 *　 - 名前
 *　 - ポケモンの説明
 *　 - ポケモン個体情報取得用URL
 *　 - 画像URL　
 */
data class PokemonListUiData(
    var id: Int = 0,
    val name: String = "",
    var displayName: String = "",
    var imageUrl: String? = "",
    val isLike: Boolean = false,
    val type:String = ""
)

/**
 * LikeのUi状態を表す
 */
data class LikeListUiState(
    val likeDetails: LikeDetails = LikeDetails(),
    val likeList: MutableList<PokemonListUiData> = mutableListOf(),
)

/**
 * 一つのLikeを表す
 */
data class LikeDetails(
    val id: Int = 0,
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
    val weight: Double = 0.0
)

/**
 * LikeDetails -> Like
 */
fun LikeDetails.toLike(): Like = Like(
    id = id,
    name = name,
    displayName = displayName,
    description = description,
    genus = genus,
//    type = type,
    hp = hp,
    attack = attack,
    defense = defense,
    speed = speed,
    imageUrl = imageUrl,
    height = height,
    weight = weight
)

/**
 * Like -> LikeUiState
 */
fun Like.toLikeUiState(isEntryValid: Boolean = false): LikeListUiState = LikeListUiState(
    likeDetails = this.toLikeDetails()
)

/**
 * Like -> LikeDetails
 */
fun Like.toLikeDetails(): LikeDetails = LikeDetails(
    id = id,
    name = name,
    displayName = displayName,
    description = description,
    genus = genus,
//    type = type,
    hp = hp,
    attack = attack,
    defense = defense,
    speed = speed,
    imageUrl = imageUrl,
    height = height,
    weight = weight
)

/**
 * LikeDetails -> PokemonListUiData
 */
fun LikeDetails.toPokemonListUiDataByLikeDetails(): PokemonListUiData = PokemonListUiData(
    id = id,
    name = name,
    displayName = displayName,
    imageUrl = imageUrl
)

/**
 * List<Like> -> MutableList<PokemonListUiData>
 */
fun List<Like>.toPokemonListUiDataList(): MutableList<PokemonListUiData> {
    val convertList = mutableListOf<PokemonListUiData>()

    convertList.addAll(
        this.map { like ->
            PokemonListUiData(
                id = like.id,
                name = like.name,
                displayName = like.displayName,
                imageUrl = like.imageUrl,
                isLike = true
            )
        }
    )
    return convertList
}