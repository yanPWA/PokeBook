package com.example.pokebook.ui.viewModel.Home

import android.os.Parcel
import android.os.Parcelable
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

/**
 * 一覧表示画面の状態に関する情報
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Fetched(
        val uiDataList: ImmutableList<PokemonListUiData>
    ) : HomeUiState()

    object InitialState : HomeUiState()
    object ResultError : HomeUiState()
}

/**
 *　id
 *　名前
 *　ポケモンの説明
 *　ポケモン個体情報取得用URL
 *　画像URL　
 */
data class PokemonListUiData(
    var pokemonNumber: Int = 0,
    val name: String = "",
    var displayName: String = "",
    val url: String = "",
    var imageUrl: String? = "",
    val speciesNumber: String? = ""
)

data class HomeScreenConditionState(
    val isScrollTop: Boolean = true,
    val pagePosition: Int = 0,
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
