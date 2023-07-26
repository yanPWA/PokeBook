package com.example.pokebook.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokebook.ui.screen.HomeScreen
import com.example.pokebook.ui.screen.PokemonDetailScreen
import com.example.pokebook.ui.theme.PokeBookTheme
import com.example.pokebook.ui.viewModel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "homeScreen") {
                composable(route = "homeScreen") {
                    PokeBookTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val homeViewModel: HomeViewModel = viewModel()
                            HomeScreen(
                                viewModel = homeViewModel,
                                onClickCard = { navController.navigate("pokemonDetailScreen") }
                            )
                        }
                    }
                }
                composable(route = "pokemonDetailScreen") {
                    PokeBookTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            PokemonDetailScreen(
                                onClickCard = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
//            PokeBookTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val homeViewModel: HomeViewModel = viewModel()
//                    HomeScreen(homeViewModel)
//                }
//            }
        }
    }
}
