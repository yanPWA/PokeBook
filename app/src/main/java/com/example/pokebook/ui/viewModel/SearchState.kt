package com.example.pokebook.ui.viewModel

import com.example.pokebook.model.PokemonListItem
import com.example.pokebook.ui.screen.TypeName
import java.util.UUID

/**
 * 検索画面の状態に関する情報
 */
sealed class SearchUiState {
    object Loading : SearchUiState()
    data class Fetched(
        val searchList:List<PokemonListUiData>
    ): SearchUiState()
    object InitialState : SearchUiState()
}

data class SearchConditionState(
    val pokemonNumber: String = "",
    val pokemonName: String ="",
    val pokemonTypeName: String = ""
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
