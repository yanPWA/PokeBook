package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailScreenUiData
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailUiEvent
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailUiState
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Like.LikeDetails
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Like.toLikeDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun PokemonDetailScreen(
    likeEntryViewModel: LikeEntryViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickBackButton: () -> Unit
) {
    PokemonDetailScreen(
        uiState = pokemonDetailViewModel.uiState,
        uiEvent = pokemonDetailViewModel.uiEvent,
        consumeEvent = pokemonDetailViewModel::processed,
        conditionState = pokemonDetailViewModel.conditionState,
        onClickBackButton = onClickBackButton,
        updateIsLike = pokemonDetailViewModel::updateIsLike,
        saveLike = likeEntryViewModel::saveLike,
        deleteLike = likeEntryViewModel::deleteLike,
        checkIfRoomLike = pokemonDetailViewModel::checkIfRoomLike
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun PokemonDetailScreen(
    uiState: StateFlow<PokemonDetailUiState>,
    uiEvent: Flow<PokemonDetailUiEvent?>,
    consumeEvent: (PokemonDetailUiEvent) -> Unit,
    conditionState: StateFlow<PokemonDetailScreenUiData>,
    onClickBackButton: () -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    saveLike: suspend (LikeDetails) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    checkIfRoomLike: suspend (Int) -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiEvent by uiEvent.collectAsStateWithLifecycle(initialValue = null)

    when (uiEvent) {
        is PokemonDetailUiEvent.Error -> {
            ErrorScreen(
                consumeEvent = consumeEvent,
                event = uiEvent as PokemonDetailUiEvent.Error
            )
        }

        null -> {}
    }


    when (state) {
        is PokemonDetailUiState.Fetched -> {
            PokemonDetailScreen(
                uiData = conditionState.value,
                onClickBackButton = onClickBackButton,
                updateIsLike = updateIsLike,
                saveLike = saveLike,
                deleteLike = deleteLike,
                checkIfRoomLike = checkIfRoomLike
            )
        }

        PokemonDetailUiState.Loading -> LoadingScreen()

        PokemonDetailUiState.ResultError -> {
            ResultError(
                text = stringResource(R.string.search_by_id_error_text),
                onClickBackSearchScreen = onClickBackButton
            )
        }

        PokemonDetailUiState.SearchError -> {
            ResultError(
                text = stringResource(R.string.search_by_name_error_text),
                onClickBackSearchScreen = onClickBackButton
            )
        }

        else -> {}
    }
}

@Composable
private fun PokemonDetailScreen(
    uiData: PokemonDetailScreenUiData,
    onClickBackButton: () -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    saveLike: suspend (LikeDetails) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    checkIfRoomLike: suspend (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(
                state = rememberScrollState(),
                reverseScrolling = true
            )
    ) {
        Image(
            imageVector = ImageVector.vectorResource(
                id = if (isSystemInDarkTheme()) R.drawable.arrow_back_fillf else R.drawable.arrow_back_fill0
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable {
                    onClickBackButton.invoke()
                }
        )
        TitleImage(
            pokemon = uiData,
            type = uiData.type.firstOrNull() ?: "",
            updateIsLike = updateIsLike,
            deleteLike = deleteLike,
            saveLike = saveLike,
            checkIfRoomLike = checkIfRoomLike
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(6.dp)
        ) {
            AutoSizeableText(
                text = String.format(
                    stringResource(id = R.string.pokemon_name),
                    uiData.pokemonNumber,
                    uiData.name
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = uiData.description,
                fontSize = 15.sp,
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onBackground
            )

            BaseInfo(
                uiData = uiData,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
            Ability(
                uiData = uiData,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun TitleImage(
    type: String,
    pokemon: PokemonDetailScreenUiData,
    updateIsLike: (Boolean, Int) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    saveLike: suspend (LikeDetails) -> Unit,
    checkIfRoomLike: suspend (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 5.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val coroutineScope = rememberCoroutineScope()

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .background(type.convertToColorCodeByTypeName())
                .background(
                    pokemon.type
                        .firstOrNull()
                        ?.convertToColorCodeByTypeName() ?: Color(
                        color = 0xFFAEAEAE
                    )
                )
        ) {
            coroutineScope.launch {
                checkIfRoomLike.invoke(pokemon.pokemonNumber)
            }
            Image(
                imageVector = ImageVector.vectorResource(
                    id = if (pokemon.isLike) R.drawable.favorite_fill else R.drawable.favorite_border
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .size(30.dp)
                    .clickable {
                        coroutineScope.launch {
                            if (pokemon.isLike) {
                                deleteLike.invoke(pokemon.toLikeDetails())
                            } else {
                                saveLike.invoke(pokemon.toLikeDetails())
                            }
                        }
                        updateIsLike.invoke(!pokemon.isLike, pokemon.pokemonNumber)
                    }
            )
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(pokemon.imageUri)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BaseInfo(
    uiData: PokemonDetailScreenUiData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = String.format(
                stringResource(R.string.pokemon_genus), uiData.genus
            ),
            fontSize = 20.sp,
            modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
        )
        TypeTag(typeList = uiData.type, modifier = modifier)
        Row(
            modifier = modifier
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = String.format(stringResource(R.string.pokemon_weight), uiData.weight),
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
            Text(
                text = String.format(stringResource(R.string.pokemon_height), uiData.height),
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
    }
}

@Composable
private fun Ability(
    uiData: PokemonDetailScreenUiData,
    modifier: Modifier = Modifier
) {
    // TODO ゆくゆくはグラフ表示させたい
    Card(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = String.format(stringResource(R.string.pokemo_hp), uiData.hp),
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = String.format(stringResource(R.string.pokemon_attack), uiData.attack),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
        Row(
            modifier = modifier
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = String.format(stringResource(R.string.pokemon_defense), uiData.defense),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
            Text(
                text = String.format(stringResource(R.string.pokemon_speed), uiData.speed),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
    }
}

/**
 * 文字サイズの自動調整
 */
@Composable
fun AutoSizeableText(
    text: String,
    color: Color,
    maxTextSize: Int = 50,
    minTextSize: Int = 40,
    modifier: Modifier
) {
    var textSize by remember { mutableStateOf(maxTextSize) }
    val checked = remember(text) { mutableMapOf<Int, Boolean?>() }
    var overflow by remember { mutableStateOf(TextOverflow.Clip) }

    androidx.compose.material.Text(
        text = text,
        color = color,
        fontSize = textSize.sp,
        maxLines = 1,
        overflow = overflow,
        modifier = modifier,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                checked[textSize] = true
                if (textSize > minTextSize) {
                    textSize -= 1
                } else {
                    overflow = TextOverflow.Ellipsis
                }
            } else {
                checked[textSize] = false
                if (textSize < maxTextSize) {
                    if (checked[textSize + 1] == null) {
                        textSize += 1
                    }
                }
            }
        }
    )
}
