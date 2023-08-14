package com.example.pokebook.ui.viewModel.Home

import java.util.UUID

/**
 * 一覧表示画面の状態に関する情報
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Fetched(
        val uiDataList: MutableList<PokemonListUiData>
    ) : HomeUiState()

    object InitialState : HomeUiState()
    object ResultError: HomeUiState()
}

/**
 *　id
 *　名前
 *　ポケモンの説明
 *　ポケモン個体情報取得用URL
 *　画像URL　
 */
data class PokemonListUiData(
    var id: Int = 0,
    val name: String = "",
    var displayName: String = "",
    val url: String = "",
    var imageUrl: String? = ""
)

data class HomeScreenConditionState(
    val count: Int = 0,
    val offset: String = "20",
    val previous: String = "",
    val currentNumberStart: String = "1",
    val isScrollTop:Boolean = true
)

/**
 * エラー表示に関する情報
 */
sealed class HomeUiEvent(
    // ワンショットのイベントとして管理
    val id: Long = UUID.randomUUID().mostSignificantBits
) {
    data class Error(val e: Throwable) : HomeUiEvent()
}