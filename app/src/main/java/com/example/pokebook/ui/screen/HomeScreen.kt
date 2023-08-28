package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Home.HomeUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onClickCard: (Int) -> Unit
) {
    HomeScreen(
        uiState = homeViewModel.uiState,
        uiEvent = homeViewModel.uiEvent,
        homeUiConditionState = homeViewModel.conditionState,
        consumeEvent = homeViewModel::processed,
        onClickNext = homeViewModel::onClickNext,
        onClickBack = homeViewModel::onClickBack,
        onClickCard = onClickCard,
        updateIsFirst = homeViewModel::updateIsFirst,
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
    onClickCard: (Int) -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    onClickRetryGetList: () -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiEvent by uiEvent.collectAsStateWithLifecycle(initialValue = null)
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

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
                startId = homeUiConditionState.value.pagePosition * 20 + 1,
                endId = homeUiConditionState.value.pagePosition * 20 + 20,
                pokemonUiDataList = (state as HomeUiState.Fetched).uiDataList,
                isFirst = homeUiConditionState.value.isScrollTop,
                onClickNext = onClickNext,
                onClickBack = onClickBack,
                onClickCard = onClickCard,
                updateIsFirst = updateIsFirst,
                lazyGridState = lazyGridState,
                coroutineScope = coroutineScope
            )
        }

        HomeUiState.Loading -> {
            Column {
                DefaultHeader(
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
    startId: Int,
    endId: Int,
    pokemonUiDataList: List<PokemonListUiData>,
    isFirst: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: (Int) -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    lazyGridState: LazyGridState,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        DefaultHeader(
            title = String.format(
                stringResource(id = R.string.header_title_displayed_number),
                startId,
                endId
            ),
            onClickNext = onClickNext,
            onClickBack = onClickBack
        )
        PokeList(
            pokemonUiDataList = pokemonUiDataList,
            isFirst = isFirst,
            onClickCard = onClickCard,
            updateIsFirst = updateIsFirst,
            lazyGridState = lazyGridState,
            coroutineScope = coroutineScope
        )
    }
}

/**
 * ポケモン画像一覧
 */
@Composable
fun PokeList(
    pokemonUiDataList: List<PokemonListUiData>,
    isFirst: Boolean,
    onClickCard: (Int) -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    lazyGridState: LazyGridState,
    coroutineScope: CoroutineScope
) {
    LaunchedEffect(lazyGridState) {
        coroutineScope.launch {
            if (isFirst) {
                lazyGridState.scrollToItem(0)
                updateIsFirst.invoke(false)
            }
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
    onClickCard: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = cardElevation(4.dp),
        onClick = { onClickCard.invoke(pokemon.pokemonNumber) }
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
