package com.example.pokebook.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pokebook.PokemonApplication
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Home.HomeViewModel
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Main.MainViewModel
import com.example.pokebook.ui.viewModel.Search.SearchViewModel

/**
 * ViewModel インスタンスを作成するファクトリーを提供
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainViewModel(
                pokemonApplication().container.pokemonDataRepository
            )
        }
        initializer {
            LikeEntryViewModel(
                pokemonApplication().container.likesRepository
            )
        }
        initializer {
            SearchViewModel(
                pokemonApplication().container.searchRepository,
                pokemonApplication().container.searchTypeListRepository,
                pokemonApplication().container.pokemonDataRepository
            )
        }
        initializer {
            HomeViewModel(
                pokemonApplication().container.homeRepository,
                pokemonApplication().container.pokemonDataRepository
            )
        }
        initializer {
            PokemonDetailViewModel(
                pokemonApplication().container.pokemonDetailRepository,
                pokemonApplication().container.pokemonDataRepository,
                pokemonApplication().container.likesRepository,
                pokemonApplication().container.pokemonDataRepository,
                pokemonApplication().container.evolutionChainRepository
            )
        }
    }
}

/**
 * [pokemonAplication]のインスタンスを返す拡張関数
 */
fun CreationExtras.pokemonApplication(): PokemonApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PokemonApplication)
