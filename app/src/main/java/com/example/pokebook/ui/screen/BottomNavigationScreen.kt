package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokebook.ui.viewModel.HomeViewModel
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.SearchViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * ボトムナビゲーション
 */
sealed class BottomNavItems(
    val route: String,
    val name: String,
    val icon: ImageVector
) {
    object Home : BottomNavItems("home", "HOME", Icons.Filled.Home)
    object Search : BottomNavItems("search", "SEARCH", Icons.Filled.Search)
    object Like : BottomNavItems("like", "LIKE", Icons.Filled.Star)
    object Setting : BottomNavItems("setting", "SETTING", Icons.Filled.Settings)
}

val navItems = listOf(
    BottomNavItems.Home,
    BottomNavItems.Search,
    BottomNavItems.Like,
    BottomNavItems.Setting
)

/**
 * NavHost に宛先を設定する
 */
@SuppressLint("ComposableDestinationInComposeScope")
@ExperimentalFoundationApi
@Composable
private fun NavigationHost(
    navController: NavHostController
) {
    val childNavController = rememberNavController()
    val searchNavController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = BottomNavItems.Home.route
    ) {
        val homeViewModel = HomeViewModel()
        val pokemonDetailViewModel = PokemonDetailViewModel()
        val searchViewModel = SearchViewModel()
        composable(BottomNavItems.Home.route) {
            NavHost(
                navController = childNavController,
                startDestination = "pokemonListScreen"
            ) {
                composable("pokemonListScreen") {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        pokemonDetailViewModel = pokemonDetailViewModel,
                        onClickCard = { childNavController.navigate("pokemonDetailScreen") }
                    )
                }
                composable("pokemonDetailScreen") {
                    PokemonDetailScreen(
                        pokemonDetailViewModel = pokemonDetailViewModel,
                        onClickBackButton = { childNavController.navigateUp() }
                    )
                }
            }
        }
        composable(BottomNavItems.Search.route) {
            NavHost(
                navController = searchNavController,
                startDestination = "searchScreen"
            ) {
                composable("searchScreen") {
                    SearchScreen(
                        searchViewModel = searchViewModel,
                        pokemonDetailViewModel =pokemonDetailViewModel,
                        onClickSearchPokemonName = {searchNavController.navigate("pokemonDetailScreen")},
                        onClickSearchPokemonNumber = {searchNavController.navigate("pokemonDetailScreen")},
                        onClickSearchTypeButton = { searchNavController.navigate("pokemonListScreen") },
//                        onClickBackButton = { searchNavController.navigateUp() }
//                    TODO 検索たぶでバックボタンおストクラッシュする
                    )
                }
                composable("pokemonListScreen") {
                    SearchListScreen(
                        searchViewModel = searchViewModel,
                        pokemonDetailViewModel = pokemonDetailViewModel,
                        onClickCard = { searchNavController.navigate("pokemonDetailScreen") },
                        onClickBackButton = { searchNavController.navigateUp() }
                    )

                }
                composable("pokemonDetailScreen") {
                    PokemonDetailScreen(
                        pokemonDetailViewModel = pokemonDetailViewModel,
                        onClickBackButton = { searchNavController.navigateUp() }
                    )
                }
            }
        }
        composable(BottomNavItems.Like.route) {
            // TODO お気に入り画面
        }
        composable(BottomNavItems.Setting.route) {
            // TODO 設定画面
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
        val currentRoute = navBackStackEntry.value?.destination

        items.forEach { navItem ->
            BottomNavigationItem(
                label = { Text(navItem.name) },
                alwaysShowLabel = true,
                selected = currentRoute?.hierarchy?.any {
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
    ) {
        StatusBarColorSample()
        NavigationHost(navController)
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
