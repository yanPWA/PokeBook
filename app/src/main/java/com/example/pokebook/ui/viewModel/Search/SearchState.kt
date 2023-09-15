package com.example.pokebook.ui.viewModel.Search

import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

/**
 * 検索画面の状態に関する情報
 */
sealed class SearchUiState {
    object Loading : SearchUiState()
    data class Fetched(
        val searchList: ImmutableList<PokemonListUiData>
    ) : SearchUiState()

    object InitialState : SearchUiState()
    object ResultError : SearchUiState()
}

data class SearchConditionState(
    val pokemonNumber: String = "",
    val pokemonName: String = "",
    val pokemonTypeName: String = "",
    val isBackButton: Boolean = false,
    val isNextButton: Boolean = false,
    val pagePosition: Int = 0,
    val maxPage: String = "",
    val isScrollTop: Boolean = true
)

/**
 * エラー表示に関する情報
 */
sealed class SearchUiEvent(
    // ワンショットのイベントとして管理
    val id: Long = UUID.randomUUID().mostSignificantBits
) {
    data class Error(val e: Throwable) : SearchUiEvent()
}
