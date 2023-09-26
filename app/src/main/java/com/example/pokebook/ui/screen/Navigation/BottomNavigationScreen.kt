package com.example.pokebook.ui.screen.Navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Home.HomeViewModel
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Search.SearchViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

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
    likeEntryViewModel: LikeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val homeDetailViewModel: PokemonDetailViewModel =
        viewModel(
            key = "home",
            factory = AppViewModelProvider.Factory
        )

    val searchDetailViewModel: PokemonDetailViewModel =
        viewModel(
            key = "search",
            factory = AppViewModelProvider.Factory
        )

    val likeDetailViewModel: PokemonDetailViewModel =
        viewModel(
            key = "like",
            factory = AppViewModelProvider.Factory
        )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeGraph(
            navController = navController,
            likeEntryViewModel = likeEntryViewModel,
            pokemonDetailViewModel = homeDetailViewModel,
        )
        searchGraph(
            navController = navController,
            searchViewModel = searchViewModel,
            likeEntryViewModel = likeEntryViewModel,
            pokemonDetailViewModel = searchDetailViewModel
        )
        likeGraph(
            navController = navController,
            likeEntryViewModel = likeEntryViewModel,
            pokemonDetailViewModel = likeDetailViewModel
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
    pokemonDetailViewModel: PokemonDetailViewModel,
    likeEntryViewModel: LikeEntryViewModel
) {
    navigation(
        startDestination = HomeScreen.PokemonListScreen.route,
        route = BottomNavItems.Home.route
    ) {
        composable(HomeScreen.PokemonListScreen.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                homeViewModel = homeViewModel,
                onClickCard = { speciesNumber, pokemonNumber ->
                    pokemonDetailViewModel.getPokemonSpeciesById(
                        pokemonNumber = pokemonNumber,
                        speciesNumber = speciesNumber
                    )
                    // 詳細画面遷移の際はスクロール位置を保持しておく
                    homeViewModel.updateIsFirst(false)
                    navController.navigate(HomeScreen.PokemonDetailScreen.route)
                }
            )
        }
        composable(route = HomeScreen.PokemonDetailScreen.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                onClickBackButton = { navController.navigateUp() },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(HomeScreen.PokemonEvolutionDetailScreen.route)
                },
                pokemonDetailViewModel = pokemonDetailViewModel
            )
        }
        composable(route = HomeScreen.PokemonEvolutionDetailScreen.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                onClickBackButton = {
                    pokemonDetailViewModel.onClickBackButton()
                    navController.navigateUp()
                },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(HomeScreen.PokemonEvolutionDetailScreen.route)
                },
                pokemonDetailViewModel = pokemonDetailViewModel
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
                onClickSearchNumber = { pokemonNumber ->
                    pokemonDetailViewModel.getPokemonSpeciesById(
                        pokemonNumber = pokemonNumber
                    )
                    navController.navigate(SearchScreen.PokemonDetailScreenByNumber.route)
                },
                onClickSearchName = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesByName(pokemonName)
                    navController.navigate(SearchScreen.PokemonDetailScreenByName.route)
                },
                onClickSearchTypeButton = { typeNumber ->
                    searchViewModel.onLoad(typeNumber)
                    navController.navigate(SearchScreen.PokemonListScreen.route)
                }
            )
        }
        composable(SearchScreen.PokemonListScreen.route) {
            SearchListScreen(
                searchViewModel = searchViewModel,
                onClickCard = { speciesNumber, pokemonNumber ->
                    pokemonDetailViewModel.getPokemonSpeciesById(
                        pokemonNumber = pokemonNumber,
                        speciesNumber = speciesNumber
                    )
                    // 詳細画面遷移の際はスクロール位置を保持しておく
                    searchViewModel.updateIsFirst(false)
                    navController.navigate(SearchScreen.PokemonDetailScreenByNumber.route)
                },
                onClickBackSearchScreen = { navController.navigateUp() }
            )
        }
        composable(SearchScreen.PokemonNotFound.route) {
            PokemonNotFound(
                onClickBackSearchScreen = { navController.navigateUp() }
            )
        }
        composable(route = SearchScreen.PokemonDetailScreenByName.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = { navController.navigateUp() },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(SearchScreen.PokemonEvolutionDetailScreen.route)
                }
            )
        }
        composable(route = SearchScreen.PokemonDetailScreenByNumber.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = { navController.navigateUp() },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(SearchScreen.PokemonEvolutionDetailScreen.route)
                }
            )
        }
        composable(route = SearchScreen.PokemonEvolutionDetailScreen.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = {
                    pokemonDetailViewModel.onClickBackButton()
                    navController.navigateUp()
                },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(SearchScreen.PokemonEvolutionDetailScreen.route)
                },
            )
        }
    }
}

/**
 * Likeタブのナビゲーショングラフ
 */
fun NavGraphBuilder.likeGraph(
    navController: NavController,
    likeEntryViewModel: LikeEntryViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel
) {
    navigation(
        startDestination = LikeScreen.LikeListScreen.route,
        route = BottomNavItems.Like.route
    ) {
        composable(LikeScreen.LikeListScreen.route) {
            likeEntryViewModel.getAllList()
            LikeEntryScreen(
                onClickCard = { speciesNumber, pokemonNumber ->
                    navController.navigate(LikeScreen.LikeDetailScreen.route)
                    pokemonDetailViewModel.getPokemonSpeciesById(
                        pokemonNumber = pokemonNumber,
                        speciesNumber = speciesNumber
                    )
                },
                onClickBackButton = { navController.navigateUp() },
                likeEntryViewModel = likeEntryViewModel
            )
        }
        composable(route = LikeScreen.LikeDetailScreen.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = { navController.navigateUp() },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(LikeScreen.PokemonEvolutionDetailScreen.route)
                },
            )
        }
        composable(route = LikeScreen.PokemonEvolutionDetailScreen.route) {
            PokemonDetailScreen(
                likeEntryViewModel = likeEntryViewModel,
                pokemonDetailViewModel = pokemonDetailViewModel,
                onClickBackButton = {
                    pokemonDetailViewModel.onClickBackButton()
                    navController.navigateUp()
                },
                onClickEvolution = { pokemonName ->
                    pokemonDetailViewModel.getPokemonSpeciesById(englishName = pokemonName)
                    pokemonDetailViewModel.saveConditionState()
                    navController.navigate(LikeScreen.PokemonEvolutionDetailScreen.route)
                }
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
            // 選択されたタブと現在表示中のタブが同じかどうか
            val isTabSelected =
                currentDestination?.hierarchy?.any { navItem.route == it.route } == true
            BottomNavigationItem(
                label = { Text(navItem.name) },
                alwaysShowLabel = true,
                selectedContentColor = if (isTabSelected) Color.Cyan else Color(0xFFFFFFFF),
                selected = isTabSelected,
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.name,
                    )
                },
                onClick = {
                    // 今選ばれてるタブと同じタブが押下されたら
                    if (isTabSelected) {
                        // 各タブのstartDestinationに移動する
                        navController.navigate(navItem.route)
                    } else {
                        navController.navigate(navItem.route) {
                            // 同じ項目を再選択するときに同じ宛先の複数のコピーを回避する
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
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

