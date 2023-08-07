package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.pokebook.ui.viewModel.HomeViewModel
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.SearchViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * NavHost に宛先を設定する
 */
@SuppressLint("ComposableDestinationInComposeScope", "ComposableNavGraphInComposeScope")
@ExperimentalFoundationApi
@Composable
private fun NavigationHost(
    startDestination: String = BottomNavItems.Home.route,
    navController: NavHostController = rememberNavController(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val homeViewModel = HomeViewModel()
    val pokemonDetailViewModel = PokemonDetailViewModel()
    val searchViewModel = SearchViewModel()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeGraph(
            navController = navController,
            homeViewModel = homeViewModel,
            pokemonDetailViewModel = pokemonDetailViewModel
        )
        searchGraph(
            navController = navController,
            searchViewModel = searchViewModel,
            pokemonDetailViewModel = pokemonDetailViewModel
        )
        composable(route = BottomNavItems.Like.route) {
            // TODO お気に入り画面
        }
        composable(route = BottomNavItems.Setting.route) {
            // TODO 設定画面
        }
    }
}

/**
 * homeタブのナビゲーショングラフ
 */
fun NavGraphBuilder.homeGraph(
    navController: NavController,
    homeViewModel: HomeViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel
) {
    navigation(
        startDestination = HomeScreen.PokemonListScreen.route,
        route = BottomNavItems.Home.route
    ) {
        composable(HomeScreen.PokemonListScreen.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickCard = { navController.navigate(HomeScreen.PokemonDetailScreen.route) }
            )
        }
        composable(HomeScreen.PokemonDetailScreen.route) {
            PokemonDetailScreen(
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    }
}

/**
 * searchタブのナビゲーショングラフ
 */
fun NavGraphBuilder.searchGraph(
    navController: NavController,
    searchViewModel: SearchViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel

) {
    navigation(
        startDestination = SearchScreen.SearchTopScreen.route,
        route = BottomNavItems.Search.route
    ) {
        composable(SearchScreen.SearchTopScreen.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickSearchPokemonName = { navController.navigate(SearchScreen.PokemonDetailScreen.route) },
                onClickSearchPokemonNumber = { navController.navigate(SearchScreen.PokemonDetailScreen.route) },
                onClickSearchTypeButton = { navController.navigate(SearchScreen.PokemonListScreen.route) },
            )
        }
        composable(SearchScreen.PokemonListScreen.route) {
            SearchListScreen(
                searchViewModel = searchViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickCard = { navController.navigate(SearchScreen.PokemonDetailScreen.route) },
                onClickBackSearchScreen = { navController.navigateUp() }
            )

        }
        composable(SearchScreen.PokemonNotFound.route) {
            PokemonNotFound(
                onClickBackSearchScreen = { navController.navigateUp() }
            )
        }
        composable(SearchScreen.PokemonDetailScreen.route) {
            PokemonDetailScreen(
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    }
}

/**
 * BottomNavigation のセットアップ
 */
@Composable
private fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItems> = navItems
) {
    BottomNavigation(
        backgroundColor = Color(0xFF0057CC),
        contentColor = Color(0xFFFFFFFF)
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination

        items.forEach { navItem ->
            BottomNavigationItem(
                label = { Text(navItem.name) },
                alwaysShowLabel = true,
                selected = currentDestination?.hierarchy?.any {
                    navItem.route == it.route
                } == true,
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.name,
                    )
                },
                onClick = {
                    navController.navigate(navItem.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * bottomBarを含むレイアウト
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalFoundationApi
@Composable
fun BottomNavigationView() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        StatusBarColorSample()
        NavigationHost(
            startDestination = BottomNavItems.Home.route,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * ステータスバーの背景色を変更
 */
@Composable
fun StatusBarColorSample() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFF0057CC))
    }
}
