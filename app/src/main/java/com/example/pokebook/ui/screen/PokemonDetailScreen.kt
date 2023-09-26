package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.model.Chain
import com.example.pokebook.model.EvolutionChain
import com.example.pokebook.model.Evolves
import com.example.pokebook.model.EvolvesSpecies
import com.example.pokebook.model.NextEvolves
import com.example.pokebook.ui.AppViewModelProvider
import com.example.pokebook.ui.screen.common.AutoSizeableText
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
    pokemonDetailViewModel: PokemonDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClickEvolution: (String) -> Unit = {},
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
        checkIfRoomLike = pokemonDetailViewModel::checkIfRoomLike,
        onClickEvolution = onClickEvolution
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
    checkIfRoomLike: suspend (Int) -> Unit,
    onClickEvolution: (String) -> Unit
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
                checkIfRoomLike = checkIfRoomLike,
                onClickEvolution = onClickEvolution
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
    onClickEvolution: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(6.dp)
                .verticalScroll(
                    state = rememberScrollState(),
                    reverseScrolling = false
                )
        ) {
            TitleImage(
                pokemon = uiData,
                type = uiData.type.firstOrNull() ?: "",
                updateIsLike = updateIsLike,
                deleteLike = deleteLike,
                saveLike = saveLike,
                checkIfRoomLike = checkIfRoomLike
            )
            AutoSizeableText(
                text = String.format(
                    stringResource(id = R.string.pokemon_name),
                    uiData.pokemonNumber,
                    uiData.japaneseName
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = uiData.description,
                fontSize = 15.sp,
                modifier = modifier
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onBackground
            )
            Column {
                MenuTitle(
                    title = "▼基本情報",
                    modifier = modifier
                )
                BaseInfo(
                    uiData = uiData,
                    modifier = modifier
                        .padding(top = 5.dp)
                )
            }
            Column {
                MenuTitle(
                    title = "▼能力",
                    modifier = modifier
                )
                Ability(
                    uiData = uiData,
                    modifier = modifier
                        .padding(top = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            Column {
                MenuTitle(
                    title = "▼進化",
                    modifier = modifier
                )
                EvolutionChain(
                    resultEvolutionChain = uiData.resultEvolutionChain.convertToShowEvolution(
                        LocalContext.current
                    ),
                    onClickEvolution = onClickEvolution,
                    modifier = modifier
                        .padding(top = 5.dp)
                )
            }
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
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = String.format(
                stringResource(R.string.pokemon_genus), uiData.genus
            ),
            fontSize = 20.sp,
            modifier = modifier.padding(start = 10.dp, top = 5.dp)
        )
        TypeTag(typeList = uiData.type, modifier = modifier)

        Text(
            text = String.format(stringResource(R.string.pokemon_weight), uiData.weight),
            fontSize = 20.sp,
            modifier = modifier
                .padding(start = 10.dp, top = 5.dp)
        )
        Text(
            text = String.format(stringResource(R.string.pokemon_height), uiData.height),
            fontSize = 20.sp,
            modifier = modifier
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
        )
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
            .fillMaxWidth(),
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
 * 進化系譜
 */
@Composable
private fun EvolutionChain(
    resultEvolutionChain: ShowEvolution,
    onClickEvolution: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ベースポケモン
            EvolutionChainImage(
                isImageUrl = !resultEvolutionChain.basePokemonImageUrl.isNullOrEmpty(),
                imageUrl = resultEvolutionChain.basePokemonImageUrl,
                pokemonName = resultEvolutionChain.basePokemonName,
                onClickEvolution = onClickEvolution,
                modifier = modifier.align(Alignment.CenterVertically)
            )
            if (resultEvolutionChain.evolution?.isNotEmpty() == true) {
                Text(
                    text = "▶︎",
                    fontSize = 20.sp,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                )
                Column {
                    resultEvolutionChain.evolution.forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 進化ポケモン
                            EvolutionChainImage(
                                isImageUrl = !it.nextPokemonImageUrl.isNullOrEmpty(),
                                imageUrl = it.nextPokemonImageUrl,
                                pokemonName = it.nextPokemonName,
                                onClickEvolution = onClickEvolution,
                                modifier = modifier.align(Alignment.CenterVertically)
                            )
                            Column {
                                it.lastPokemonImageUrl?.forEachIndexed { index, item ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "︎︎▶︎",
                                            fontSize = 20.sp,
                                        )
                                        // 最終進化ポケモン
                                        EvolutionChainImage(
                                            isImageUrl = item.isNotEmpty(),
                                            imageUrl = item,
                                            pokemonName = it.lastPokemonName?.get(index),
                                            onClickEvolution = onClickEvolution,
                                            modifier = modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * メニュー
 */
@Composable
private fun MenuTitle(
    title: String,
    modifier: Modifier
) {
    Text(
        text = title,
        fontSize = 15.sp,
        modifier = modifier
            .padding(start = 20.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.onBackground
    )
}

/**
 *
 */
@Composable
private fun EvolutionChainImage(
    isImageUrl: Boolean,
    imageUrl: String?,
    pokemonName: String?,
    onClickEvolution: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isImageUrl) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                modifier = modifier
                    .size(100.dp)
                    .padding(bottom = 5.dp)
                    .clickable {
                        // 名前検索
                        pokemonName?.let { name ->
                            onClickEvolution.invoke(name)
                        }
                    },
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            Text(
                text = "名前",
                fontSize = 15.sp,
                modifier = modifier
            )
        }

    } else {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_hide_image_24),
            modifier = modifier
                .size(100.dp),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

/**
 * 進化系譜サンプルデータ TODO API繋ぎ込んだら削除　完成するまでは一旦残す
 */
private fun createEvolutionChain(): EvolutionChain {
    return EvolutionChain(
        Chain(
            evolves =
//            null,
            listOf(
                Evolves(
                    evolves = listOf(
                        NextEvolves(
                            lastGeneration = EvolvesSpecies(
                                name = "charizard",
                                url = "https://pokeapi.co/api/v2/pokemon-species/6/"
                            )
                        ),
                        NextEvolves(
                            lastGeneration = EvolvesSpecies(
                                name = "kangaskhan",
                                url = "https://pokeapi.co/api/v2/pokemon-species/115/"
                            )
                        )
                    ),
                    nextGeneration = EvolvesSpecies(
                        name = "charmeleon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/5/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "sylveon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/700/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "glaceon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/471/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "leafeon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/470/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "umbreon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/197/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "espeon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/196/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "flareon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/136/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "jolteon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/135/"
                    )
                ),
                Evolves(
                    evolves = null,
                    nextGeneration = EvolvesSpecies(
                        name = "vaporeon",
                        url = "https://pokeapi.co/api/v2/pokemon-species/134/"
                    )
                ),
            ),
            basePokemon = EvolvesSpecies(
                name = "eevee",
                url = "https://pokeapi.co/api/v2/pokemon-species/133/"
            )
////        basePokemon = EvolvesSpecies(
////            name = "kangaskhan",
////            url = "https://pokeapi.co/api/v2/pokemon-species/115/"
////        )
        )
    )
}

/**
 * 進化系譜の表示用コンバーター
 */
fun EvolutionChain.convertToShowEvolution(context: Context): ShowEvolution {
    // ベースポケモンの情報
    val basePokemonSpeciesNumber =
        this.chain?.basePokemon?.let { Uri.parse(it.url).lastPathSegment }
    val basePokemonImageUrl =
        context.getString(R.string.nextPokemonImageUrl, basePokemonSpeciesNumber)

    // 進化系ポケモンリスト
    val evolutionList = this.chain?.evolves?.mapNotNull { nextEvolve ->
        val nextPokemonName = nextEvolve.nextGeneration?.name
        val nextPokemonSpeciesNumber = Uri.parse(nextEvolve.nextGeneration?.url).lastPathSegment
        val nextPokemonImageUrl =
            context.getString(R.string.nextPokemonImageUrl, nextPokemonSpeciesNumber)
        val lastPokemonName = nextEvolve.evolves?.mapNotNull { lastEvolve ->
            lastEvolve.lastGeneration.name
        }
        val lastPokemonSpeciesNumber =
            nextEvolve.evolves?.mapNotNull { lastEvolve ->
                Uri.parse(lastEvolve.lastGeneration.url).lastPathSegment
            }
        val lastPokemonImageUrl =
            lastPokemonSpeciesNumber?.map { lastEvolve ->
                context.getString(R.string.nextPokemonImageUrl, lastEvolve)
            }
        if (!nextPokemonSpeciesNumber.isNullOrEmpty() || !lastPokemonSpeciesNumber.isNullOrEmpty()) {
            Evolution(
                nextPokemonName = nextPokemonName,
                nextPokemonSpeciesNumber = nextPokemonSpeciesNumber,
                nextPokemonImageUrl = nextPokemonImageUrl,
                lastPokemonName = lastPokemonName,
                lastPokemonSpeciesNumber = lastPokemonSpeciesNumber,
                lastPokemonImageUrl = lastPokemonImageUrl
            )
        } else {
            null
        }
    }

    return ShowEvolution(
        basePokemonName = this.chain?.basePokemon?.name ?: "",
        basePokemonSpeciesNumber = basePokemonSpeciesNumber,
        basePokemonImageUrl = basePokemonImageUrl,
        evolution = evolutionList
    )
}

/**
 * 進化系表示用クラス
 */
data class ShowEvolution(
    val basePokemonName: String?,
    val basePokemonSpeciesNumber: String?,
    val basePokemonImageUrl: String?,
    val evolution: List<Evolution>?
)

data class Evolution(
    val nextPokemonName: String?,
    val nextPokemonSpeciesNumber: String?,
    val nextPokemonImageUrl: String?,
    val lastPokemonName: List<String>?,
    val lastPokemonSpeciesNumber: List<String>?,
    val lastPokemonImageUrl: List<String>?,
)
