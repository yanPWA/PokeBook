package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.HomeScreenConditionState
import com.example.pokebook.ui.viewModel.PokemonListUiData
import com.example.pokebook.ui.viewModel.HomeUiState
import com.example.pokebook.ui.viewModel.HomeViewModel
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickCard: () -> Unit
) {
    HomeScreen(
        uiState = homeViewModel.uiState,
        homeUiConditionState = homeViewModel.conditionState,
        onClickNext = homeViewModel::onClickNext,
        onClickBack = homeViewModel::onClickBack,
        onClickCard = onClickCard,
        updateIsFirst = homeViewModel::updateIsFirst,
        getPokemonSpecies = pokemonDetailViewModel::getPokemonSpeciesByNumber
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun HomeScreen(
    uiState: StateFlow<HomeUiState>,
    homeUiConditionState: StateFlow<HomeScreenConditionState>,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: () -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    getPokemonSpecies: (PokemonListUiData) -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    when (state) {
        is HomeUiState.Fetched -> {
            PokeList(
                currentNumberStart = homeUiConditionState.value.currentNumberStart,
                currentNumberEnd = homeUiConditionState.value.offset,
                pokemonUiDataList = (state as HomeUiState.Fetched).uiDataList,
                isFirst = homeUiConditionState.value.isFirst,
                onClickNext = onClickNext,
                onClickBack = onClickBack,
                onClickCard = onClickCard,
                updateIsFirst = updateIsFirst,
                getPokemonSpecies = getPokemonSpecies,
                lazyGridState = lazyGridState,
                coroutineScope = coroutineScope
            )
        }

        HomeUiState.Loading -> {
            Column {
                DefaultHeader(
                    title = String.format(
                        stringResource(id = R.string.header_title_displayed_number),
                        homeUiConditionState.value.currentNumberStart,
                        homeUiConditionState.value.offset
                    ),
                    onClickNext = onClickNext,
                    onClickBack = onClickBack,
                )
                LoadingScreen()
            }
        }

        else -> {}
    }
}

@Composable
private fun PokeList(
    currentNumberStart: String,
    currentNumberEnd: String,
    pokemonUiDataList: List<PokemonListUiData>,
    isFirst: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: () -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    getPokemonSpecies: (PokemonListUiData) -> Unit,
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
                currentNumberStart,
                currentNumberEnd
            ),
            onClickNext = onClickNext,
            onClickBack = onClickBack
        )
        PokeList(
            pokemonUiDataList = pokemonUiDataList,
            isFirst = isFirst,
            onClickCard = onClickCard,
            updateIsFirst = updateIsFirst,
            getPokemonSpecies = getPokemonSpecies,
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
    onClickCard: () -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    getPokemonSpecies: (PokemonListUiData) -> Unit,
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
                getPokemonSpecies = getPokemonSpecies
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeCard(
    pokemon: PokemonListUiData,
    onClickCard: () -> Unit,
    getPokemonSpecies: (PokemonListUiData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = cardElevation(4.dp),
        onClick = {
            getPokemonSpecies.invoke(pokemon)
            onClickCard.invoke()
        }
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
                    pokemon.id,
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

/**
 * ポケモン一覧のヘッダー領域
 */
@Composable
fun DefaultHeader(
    title: String,
    updateButtonStates: (Boolean, Boolean) -> Unit = { _, _ -> },
    onClickNext: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.background),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .wrapContentHeight()
                    .clickable {
                        updateButtonStates.invoke(true, false)
                        onClickBack.invoke()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.back_button),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .weight(1F)
                    .wrapContentHeight()
                    .clickable {
                        updateButtonStates.invoke(false, true)
                        onClickNext.invoke()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.next_button),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun PokeCardPreview() {
    PokeCard(
        pokemon = PokemonListUiData(name = "ピカチュウ"),
        onClickCard = {},
        getPokemonSpecies = {}
    )
}
