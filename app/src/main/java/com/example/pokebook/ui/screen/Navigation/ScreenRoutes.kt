package com.example.pokebook.ui.screen.Navigation

/**
 * HOMEタブ
 */
sealed class HomeScreen(val route: String) {
    object PokemonListScreen : HomeScreen("home/pokemonListScreen")
    object PokemonDetailScreen : HomeScreen("home/pokemonDetailScreen")
}

/**
 * SEARCHタブ
 */
sealed class SearchScreen(val route: String) {
    object SearchTopScreen : SearchScreen("search/searchScreen")
    object PokemonListScreen : SearchScreen("search/pokemonListScreen")
    object PokemonNotFound : SearchScreen("search/pokemonNotFound")
    object PokemonDetailScreen : SearchScreen("search/pokemonDetailScreen")
}

/**
 * LIKEタブ
 */
sealed class LikeScreen(val route: String) {
    object LikeListScreen : LikeScreen("like/listScreen")
    object LikeDetailScreen : LikeScreen("like/detailScreen")
}


/**
 * SETTINGタブ
 */
