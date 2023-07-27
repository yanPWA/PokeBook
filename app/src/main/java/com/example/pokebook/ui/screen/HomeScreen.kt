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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.HomeScreenConditionState
import com.example.pokebook.ui.viewModel.HomeScreenUiData
import com.example.pokebook.ui.viewModel.HomeUiState
import com.example.pokebook.ui.viewModel.HomeViewModel
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onClickCard: () -> Unit
) {
    HomeScreen(
        uiState = viewModel.uiState,
        conditionState = viewModel.conditionState,
        onClickNext = viewModel::onClickNext,
        onClickBack = viewModel::onClickBack,
        onClickCard = onClickCard
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun HomeScreen(
    uiState: StateFlow<HomeUiState>,
    conditionState: StateFlow<HomeScreenConditionState>,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onClickCard: () -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()

    when (state) {
        is HomeUiState.Fetched -> {
            PokeList(
                currentNumberStart = conditionState.value.currentNumberStart,
                currentNumberEnd = conditionState.value.offset,
                pokemonUiDataList = (state as HomeUiState.Fetched).uiDataList,
                onClickNext = onClickNext,
                onClickBack = onClickBack,
                onClickCard = onClickCard
            )
        }

        HomeUiState.Loading -> LoadingScreen()
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
    onClickCard: () -> Unit
) {
    Column {
        Text(
            text = String.format(
                stringResource(id = R.string.displayedNumber),
                currentNumberStart,
                currentNumberEnd
            ),
            color = Color.Black,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.White)
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
        PokeList(
            pokemonUiDataList = pokemonUiDataList,
            onClickCard = onClickCard
        )
    }
}

/**
 * ポケモン画像一覧
 */
@Composable
private fun PokeList(
    pokemonUiDataList: List<HomeScreenUiData>,
    onClickCard: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
    ) {
        items(pokemonUiDataList) { listItem ->
            PokeCard(
                pokemon = listItem,
                onClickCard = onClickCard
            )
        }
        item { EmptySpace() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokeCard(
    pokemon: HomeScreenUiData,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = cardElevation(4.dp),
        onClick = { onClickCard.invoke() }
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
                text = pokemon.name,
                fontSize = 13.sp,
                modifier = Modifier
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(bottom = 2.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(3.dp)
            )
        }
    }
}

@Composable
private fun EmptySpace() {
    Spacer(modifier = Modifier.size(20.dp))
}

@Preview
@Composable
private fun PokeCardPreview() {
    PokeCard(
        pokemon = HomeScreenUiData(name = "ピカチュウ"),
        onClickCard = {}
    )
}
