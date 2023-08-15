package com.example.pokebook.ui.viewModel.Detail

import java.util.UUID

/**
 * 詳細画面の状態に関する情報
 */
sealed class PokemonDetailUiState {
    object Loading : PokemonDetailUiState()
    object Fetched : PokemonDetailUiState()
    object InitialState : PokemonDetailUiState()
    object ResultError: PokemonDetailUiState()
}


/**
 *　ID
 *　名前
 *　説明
 *　分類
 *　属性
 *　HP
 *　攻撃
 *　防御
 *　スピード
 *　身長
 *　体重
 */
data class PokemonDetailScreenUiData(
    val pokemonNumber: Int = 0,
    val name: String = "",
    val description: String = "",
    val genus: String = "",
    val type: List<String> = emptyList(),
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val speed: Int = 0,
    val imageUri: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val isLike:Boolean = false
)

/**
 * エラー表示に関する情報
 */
sealed class PokemonDetailUiEvent(
    // ワンショットのイベントとして管理
    val id: Long = UUID.randomUUID().mostSignificantBits
) {
    data class Error(val e: Throwable) : PokemonDetailUiEvent()
}