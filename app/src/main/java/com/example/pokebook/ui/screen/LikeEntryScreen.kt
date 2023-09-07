package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData

import com.example.pokebook.ui.viewModel.Like.LikeDetails
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Like.LikeUiEvent
import com.example.pokebook.ui.viewModel.Like.LikeUiState
import com.example.pokebook.ui.viewModel.Like.toPokemonListUiDataByLikeDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun LikeEntryScreen(
    onClickCard: (Int, Int) -> Unit,
    onClickBackButton: () -> Unit,
    likeEntryViewModel: LikeEntryViewModel
) {
    LikeEntryBody(
        uiState = likeEntryViewModel.uiState,
        uiEventState = likeEntryViewModel.uiEvent,
        consumeEvent = likeEntryViewModel::processed,
        onClickCard = onClickCard,
        onClickBackButton = onClickBackButton,
        updateIsLike = likeEntryViewModel::updateIsLike,
        deleteLike = likeEntryViewModel::deleteLike,
        getAllList = likeEntryViewModel::getAllList,
    )
}

@Composable
private fun LikeEntryBody(
    uiState: StateFlow<LikeUiState>,
    uiEventState: Flow<LikeUiEvent?>,
    consumeEvent: (LikeUiEvent) -> Unit,
    onClickCard: (Int, Int) -> Unit,
    onClickBackButton: () -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    getAllList: () -> Unit,
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiEvent by uiEventState.collectAsStateWithLifecycle(initialValue = null)

    when (uiEvent) {
        is LikeUiEvent.Error -> {
            ErrorScreen(
                consumeEvent = consumeEvent,
                event = uiEvent as LikeUiEvent.Error
            )
        }

        null -> {}
    }

    when (state) {
        is LikeUiState.Loading -> {
            LoadingScreen()
        }

        is LikeUiState.Fetched -> {
            if ((state as LikeUiState.Fetched).uiDataList.isEmpty()) {
                PokemonNotFound(
                    onClickBackSearchScreen = onClickBackButton
                )
            } else {
                LikeScreen(
                    likeList = (state as LikeUiState.Fetched).uiDataList,
                    onClickCard = onClickCard,
                    updateIsLike = updateIsLike,
                    deleteLike = deleteLike,
                    getAllList = getAllList,
                )
            }
        }

        is LikeUiState.ResultError -> {
            ResultError(
                text = stringResource(R.string.result_error_text),
                onClickBackSearchScreen = onClickBackButton
            )
        }

        else -> {
        }
    }
}

@Composable
fun LikeScreen(
    likeList: MutableList<LikeDetails>,
    onClickCard: (Int, Int) -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    getAllList: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val lazyGridState = rememberLazyGridState()

        LikeHeader(
            title = stringResource(R.string.like_header_title),
            modifier = Modifier.padding(top = 5.dp)
        )

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(minSize = 150.dp),
        ) {
            items(likeList) { listItem ->
                LikePokeCard(
                    pokemon = listItem,
                    onClickCard = onClickCard,
                    updateIsLike = updateIsLike,
                    deleteLike = deleteLike,
                    getAllList = getAllList,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikePokeCard(
    pokemon: LikeDetails,
    onClickCard: (Int, Int) -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    getAllList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            onClickCard.invoke(pokemon.speciesNumber, pokemon.pokemonNumber)
        },
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            val coroutineScope = rememberCoroutineScope()

            if (pokemon.imageUrl.isEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.no_image),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                Box(
                    contentAlignment = Alignment.TopEnd,
                ) {
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
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.favorite_fill),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(30.dp)
                            .clickable {
                                coroutineScope.launch {
                                    deleteLike.invoke(pokemon)
                                    updateIsLike.invoke(!pokemon.isLike, pokemon.pokemonNumber)
                                    getAllList.invoke()
                                }
                            }
                    )
                }
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

fun createDummyList(): MutableList<LikeDetails> {
    val list = mutableListOf<LikeDetails>()
    for (i in 0 until 11) {
        list.add(
            LikeDetails(
                pokemonNumber = i,
                name = "pika",
                displayName = "ピカチュウ",
                imageUrl = "",
                isLike = true,
//                type = "electric"
            )
        )
    }
    return list
}

/**
 * ヘッダー部分
 */
@Composable
fun LikeHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            textAlign = TextAlign.Center
        )
    }
}
