package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.Home.HomeScreenConditionState
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import com.example.pokebook.ui.viewModel.Home.HomeUiState
import com.example.pokebook.ui.viewModel.Home.HomeViewModel
import com.example.pokebook.ui.viewModel.Home.HomeUiEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onClickCard: (Int, Int) -> Unit
) {
    HomeScreen(
        uiState = homeViewModel.uiState,
        uiEvent = homeViewModel.uiEvent,
        homeUiConditionState = homeViewModel.conditionState,
        consumeEvent = homeViewModel::processed,
        onClickNext = homeViewModel::onClickNext,
        onClickBack = homeViewModel::onClickBack,
        onClickCard = onClickCard,
        onClickRetryGetList = homeViewModel::getPokemonList
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun HomeScreen(
    uiState: StateFlow<HomeUiState>,
    uiEvent: Flow<HomeUiEvent?>,
    homeUiConditionState: StateFlow<HomeScreenConditionState>,
    consumeEvent: (HomeUiEvent) -> Unit,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: (Int, Int) -> Unit,
    onClickRetryGetList: () -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiEvent by uiEvent.collectAsStateWithLifecycle(initialValue = null)
    val lazyGridState = rememberLazyGridState()

    when (uiEvent) {
        is HomeUiEvent.Error -> {
            ErrorScreen(
                consumeEvent = consumeEvent,
                event = uiEvent as HomeUiEvent.Error
            )
        }

        null -> {}
    }

    when (state) {
        is HomeUiState.Fetched -> {
            PokeList(
                pagePosition = homeUiConditionState.value.pagePosition,
                pokemonUiDataList = (state as HomeUiState.Fetched).uiDataList,
                isScrollTop = homeUiConditionState.value.isScrollTop,
                onClickNext = onClickNext,
                onClickBack = onClickBack,
                onClickCard = onClickCard,
                lazyGridState = lazyGridState,
            )
        }

        HomeUiState.Loading -> {
            Column {
                DefaultHeader(
                    pagePosition = homeUiConditionState.value.pagePosition,
                    title = String.format(
                        stringResource(id = R.string.header_title_displayed_number),
                        homeUiConditionState.value.pagePosition * 20 + 1,
                        homeUiConditionState.value.pagePosition * 20 + 20
                    ),
                    onClickNext = onClickNext,
                    onClickBack = onClickBack,
                )
                LoadingScreen()
            }
        }

        HomeUiState.ResultError -> {
            HomeResultError(
                onClickRetryGetList = onClickRetryGetList,
                isFirst = homeUiConditionState.value.isScrollTop
            )
        }

        else -> {}
    }
}

@Composable
private fun PokeList(
    pagePosition: Int,
    pokemonUiDataList: ImmutableList<PokemonListUiData>,
    isScrollTop: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: (Int, Int) -> Unit,
    lazyGridState: LazyGridState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DefaultHeader(
            pagePosition = pagePosition,
            title = String.format(
                stringResource(id = R.string.header_title_displayed_number),
                pagePosition * 20 + 1,
                pagePosition * 20 + 20,
            ),
            onClickNext = onClickNext,
            onClickBack = onClickBack
        )
        PokeList(
            pokemonUiDataList = pokemonUiDataList,
            isScrollTop = isScrollTop,
            pagePosition = pagePosition,
            onClickCard = onClickCard,
            lazyGridState = lazyGridState,
        )
    }
}

/**
 * ポケモン画像一覧
 */
@Composable
fun PokeList(
    pokemonUiDataList: ImmutableList<PokemonListUiData>,
    isScrollTop: Boolean,
    pagePosition:Int,
    onClickCard: (Int, Int) -> Unit,
    lazyGridState: LazyGridState,
) {
    // スクロールを先頭に戻すかどうか
    if (isScrollTop) {
        LaunchedEffect(pagePosition) {
            lazyGridState.scrollToItem(0)
        }
    }
    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(minSize = 150.dp),
    ) {
        items(pokemonUiDataList) { listItem ->
            PokeCard(
                pokemon = listItem,
                onClickCard = onClickCard,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeCard(
    pokemon: PokemonListUiData,
    onClickCard: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = cardElevation(4.dp),
        onClick = { onClickCard.invoke(pokemon.speciesNumber?.toInt() ?: 0, pokemon.pokemonNumber) }
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            if (pokemon.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.no_image),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(pokemon.imageUrl)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
            Text(
                text = String.format(
                    stringResource(R.string.pokemon_name),
                    pokemon.pokemonNumber,
                    pokemon.displayName
                ),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(bottom = 2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(3.dp)
            )
        }
    }
}
