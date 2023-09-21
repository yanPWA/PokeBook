package com.example.pokebook.ui.screen.Navigation

/**
 * HOMEタブ
 */
sealed class HomeScreen(val route: String) {
    object PokemonListScreen : HomeScreen("home/pokemonListScreen")
    object PokemonDetailScreen : HomeScreen("home/pokemonDetailScreen")
    object PokemonEvolutionDetailScreen:HomeScreen("home/pokemonEvolutionDetailScreen")
}

/**
 * SEARCHタブ
 */
sealed class SearchScreen(val route: String) {
    object SearchTopScreen : SearchScreen("search/searchScreen")
    object PokemonListScreen : SearchScreen("search/pokemonListScreen")
    object PokemonNotFound : SearchScreen("search/pokemonNotFound")
    object PokemonDetailScreenByName : SearchScreen("search/pokemonDetailScreenByName")
    object PokemonDetailScreenByNumber : SearchScreen("search/pokemonDetailScreenByNumber")
    object PokemonEvolutionDetailScreen:SearchScreen("search/pokemonEvolutionDetailScreen")
}

/**
 * LIKEタブ
 */
sealed class LikeScreen(val route: String) {
    object LikeListScreen : LikeScreen("like/listScreen")
    object LikeDetailScreen : LikeScreen("like/pokemonDetailScreen")
    object PokemonEvolutionDetailScreen:LikeScreen("like/pokemonEvolutionDetailScreen")
}


/**
 * SETTINGタブ
 */
