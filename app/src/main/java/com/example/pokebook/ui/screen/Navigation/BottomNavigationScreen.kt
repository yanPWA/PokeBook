package com.example.pokebook.ui.screen.Navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.pokebook.ui.AppViewModelProvider
import com.example.pokebook.ui.screen.HomeScreen
import com.example.pokebook.ui.screen.LikeEntryScreen
import com.example.pokebook.ui.screen.PokemonDetailScreen
import com.example.pokebook.ui.screen.PokemonNotFound
import com.example.pokebook.ui.screen.SearchListScreen
import com.example.pokebook.ui.screen.SearchScreen
import com.example.pokebook.ui.viewModel.Home.HomeViewModel
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Search.SearchViewModel
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
    searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    pokemonDetailViewModel: PokemonDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    likeEntryViewModel: LikeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeGraph(
            navController = navController,
            homeViewModel = homeViewModel,
            pokemonDetailViewModel = pokemonDetailViewModel,
            likeEntryViewModel = likeEntryViewModel
        )
        searchGraph(
            navController = navController,
            searchViewModel = searchViewModel,
            pokemonDetailViewModel = pokemonDetailViewModel,
            likeEntryViewModel = likeEntryViewModel
        )
        likeGraph(
            navController = navController,
            pokemonDetailViewModel = pokemonDetailViewModel,
            likeEntryViewModel = likeEntryViewModel
        )

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
    pokemonDetailViewModel: PokemonDetailViewModel,
    likeEntryViewModel: LikeEntryViewModel
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
                likeEntryViewModel = likeEntryViewModel,
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
    pokemonDetailViewModel: PokemonDetailViewModel,
    likeEntryViewModel: LikeEntryViewModel
) {
    navigation(
        startDestination = SearchScreen.SearchTopScreen.route,
        route = BottomNavItems.Search.route
    ) {
        composable(SearchScreen.SearchTopScreen.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
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
                likeEntryViewModel = likeEntryViewModel,
                onClickBackButton = { navController.navigateUp() }
            )
        }
    }
}

/**
 * Likeタブのナビゲーショングラフ
 */
fun NavGraphBuilder.likeGraph(
    navController: NavController,
    pokemonDetailViewModel: PokemonDetailViewModel,
    likeEntryViewModel: LikeEntryViewModel
) {
    navigation(
        startDestination = LikeScreen.LikeListScreen.route,
        route = BottomNavItems.Like.route
    ) {
        composable(LikeScreen.LikeListScreen.route) {
            likeEntryViewModel.getAllList()
            LikeEntryScreen(
                onClickCard = { navController.navigate(LikeScreen.LikeDetailScreen.route) },
                onClickBackButton = { navController.navigateUp() },
                pokemonDetailViewModel = pokemonDetailViewModel,
                likeEntryViewModel = likeEntryViewModel
            )
        }
        composable(LikeScreen.LikeDetailScreen.route) {
            PokemonDetailScreen(
                pokemonDetailViewModel = pokemonDetailViewModel,
                likeEntryViewModel = likeEntryViewModel,
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
