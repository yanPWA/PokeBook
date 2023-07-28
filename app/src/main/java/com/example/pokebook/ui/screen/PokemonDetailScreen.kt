package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.PokemonDetailScreenUiData
import com.example.pokebook.ui.viewModel.PokemonDetailUiState
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonDetailScreen(
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickCard: () -> Unit
) {
    PokemonDetailScreen(
        uiState = pokemonDetailViewModel.uiState,
        conditionState = pokemonDetailViewModel.conditionState,
        onClickCard = onClickCard
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun PokemonDetailScreen(
    uiState: StateFlow<PokemonDetailUiState>,
    conditionState: StateFlow<PokemonDetailScreenUiData>,
    onClickCard: () -> Unit,
) {
    val state by uiState.collectAsStateWithLifecycle()

    when (state) {
        is PokemonDetailUiState.Fetched -> {
            PokemonDetailScreen(
                uiData = conditionState.value,
                onClickCard = onClickCard
            )
        }

        PokemonDetailUiState.Loading -> LoadingScreen()
        else -> {}
    }
}

@Composable
private fun PokemonDetailScreen(
    uiData: PokemonDetailScreenUiData,
    onClickCard: () -> Unit,
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
                id = R.drawable.arrow_back_fill0_wght400_grad0_opsz48
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onClickCard.invoke() }
        )
        TitleImage(
            imageUri = uiData.imageUri
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Text(
                text = String.format(
                    stringResource(id = R.string.pokemon_name),
                    uiData.id,
                    uiData.name
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 50.sp,
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
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
            // 下部がボトムナビゲーションとかぶってしまった為
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun TitleImage(
    imageUri: String
) {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 5.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .background(Color.Yellow)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.favorite_fill0_wght400_grad0_opsz48
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .size(30.dp)
            )
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUri)
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
        Text(
            text = String.format(stringResource(R.string.pokemon_type), uiData.type),
            fontSize = 20.sp,
            modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
        )
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

@Preview
@Composable
private fun PokemonDetailScreenPreview() {
    PokemonDetailScreen(
        uiData = PokemonDetailScreenUiData(),
        onClickCard = {},
    )
}
