package com.example.pokebook.ui.viewModel

sealed interface HomeScreenLoading

/**
 * 一覧表示に必要な情報
 */
data class PokemonUiData(
    val name:String = "",
    val url: String = "",
    var imageUri: String = "",
)

/**
 *  ポケモン一覧取得時の通信状態
 */
sealed interface ApiState {
    data class Success<T>(val result: T) : ApiState
    object Error : ApiState
    object Loading : ApiState
}