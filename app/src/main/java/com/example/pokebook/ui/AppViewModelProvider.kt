package com.example.pokebook.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pokebook.PokemonApplication
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel

/**
 * ViewModel インスタンスを作成するファクトリーを提供
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for LikeViewModel
        initializer {
            LikeEntryViewModel(
                pokemonApplication().container.likesRepository
            )
        }
    }
}

/**
 * [pokemonAplication]のインスタンスを返す拡張関数
 */
fun CreationExtras.pokemonApplication(): PokemonApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PokemonApplication)
