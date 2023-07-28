package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.pokebook.ui.viewModel.HomeScreenUiData
import com.example.pokebook.ui.viewModel.HomeUiState
import com.example.pokebook.ui.viewModel.HomeViewModel
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickCard: () -> Unit
) {
    HomeScreen(
        uiState = homeViewModel.uiState,
        conditionState = homeViewModel.conditionState,
        onClickNext = homeViewModel::onClickNext,
        onClickBack = homeViewModel::onClickBack,
        onClickCard = onClickCard,
        getPokemonDescription = pokemonDetailViewModel::getPokemonDescription
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun HomeScreen(
    uiState: StateFlow<HomeUiState>,
    conditionState: StateFlow<HomeScreenConditionState>,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: () -> Unit,
    getPokemonDescription: (String) -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    when (state) {
        is HomeUiState.Fetched -> {
            PokeList(
                currentNumberStart = conditionState.value.currentNumberStart,
                currentNumberEnd = conditionState.value.offset,
                pokemonUiDataList = (state as HomeUiState.Fetched).uiDataList,
                onClickNext = onClickNext,
                onClickBack = onClickBack,
                onClickCard = onClickCard,
                getPokemonDescription = getPokemonDescription,
                lazyListState = lazyGridState
            )
        }

        HomeUiState.Loading -> {
            Column {
                DefaultHeader(
                    currentNumberStart = conditionState.value.currentNumberStart,
                    currentNumberEnd = conditionState.value.offset,
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
    pokemonUiDataList: List<HomeScreenUiData>,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: () -> Unit,
    getPokemonDescription: (String) -> Unit,
    lazyListState: LazyGridState
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        DefaultHeader(
            currentNumberStart = currentNumberStart,
            currentNumberEnd = currentNumberEnd,
            onClickNext = onClickNext,
            onClickBack = onClickBack,
        )
        PokeList(
            pokemonUiDataList = pokemonUiDataList,
            onClickCard = onClickCard,
            getPokemonDescription = getPokemonDescription,
            lazyListState = lazyListState
        )
    }
}

/**
 * ポケモン画像一覧
 */
@Composable
private fun PokeList(
    pokemonUiDataList: List<HomeScreenUiData>,
    onClickCard: () -> Unit,
    getPokemonDescription: (String) -> Unit,
    lazyListState: LazyGridState
) {
    LazyVerticalGrid(
        state = lazyListState,
        columns = GridCells.Adaptive(minSize = 150.dp),
    ) {
        items(pokemonUiDataList) { listItem ->
            PokeCard(
                pokemon = listItem,
                onClickCard = onClickCard,
                getPokemonDescription = getPokemonDescription
            )
        }
        // 下部がボトムナビゲーションとかぶってしまった為
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokeCard(
    pokemon: HomeScreenUiData,
    onClickCard: () -> Unit,
    getPokemonDescription: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = cardElevation(4.dp),
        onClick = {
            getPokemonDescription.invoke(pokemon.name)
            onClickCard.invoke()
        }
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(pokemon.imageUri)
                    .crossfade(true)
                    .build(),
                modifier = Modifier.padding(bottom = 20.dp),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
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
                        color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
                    .padding(3.dp)
            )
        }
    }
}

@Composable
private fun DefaultHeader(
    currentNumberStart: String,
    currentNumberEnd: String,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 5.dp)
    ) {
        Text(
            text = String.format(
                stringResource(id = R.string.displayedNumber),
                currentNumberStart,
                currentNumberEnd
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.background)
        )
        Row(
            modifier = Modifier
                .padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .wrapContentHeight()
                    .clickable { onClickBack.invoke() },
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
                    .clickable { onClickNext.invoke() },
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
        pokemon = HomeScreenUiData(name = "ピカチュウ"),
        onClickCard = {},
        getPokemonDescription = {}
    )
}
