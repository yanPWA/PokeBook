package com.example.pokebook.ui.viewModel

import java.util.UUID

/**
 * 一覧表示画面の状態に関する情報
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Fetched(
        val uiDataList: MutableList<HomeScreenUiData>
    ) : HomeUiState()

    object InitialState : HomeUiState()
}

/**
 * 一覧表示に必要な情報
 */
data class HomeScreenUiData(
    val name: String = "",
    val url: String = "",
    var imageUri: String = ""
)

data class HomeScreenConditionState(
    val count: Int = 0,
    val offset: String = "",
    val previous: String = "",
    val currentNumberStart: String = "1",
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


/**
 *  ポケモン一覧取得時の通信状態
 */
sealed interface ApiState {
    data class Success<T>(val result: T) : ApiState
    object Error : ApiState
    object Loading : ApiState
}
