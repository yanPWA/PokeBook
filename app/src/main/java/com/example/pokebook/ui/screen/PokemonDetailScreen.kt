package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.DefaultRequestOptions
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.model.Chain
import com.example.pokebook.model.EvolutionChain
import com.example.pokebook.model.Evolves
import com.example.pokebook.model.EvolvesSpecies
import com.example.pokebook.model.NextEvolves
import com.example.pokebook.ui.AppViewModelProvider
import com.example.pokebook.ui.screen.common.AutoSizeableText
import com.example.pokebook.ui.viewModel.Detail.DisplayEvolution
import com.example.pokebook.ui.viewModel.Detail.EvolutionChainUiState
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailScreenUiData
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailUiEvent
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailUiState
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Like.LikeDetails
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Like.toLikeDetails
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val DURATION_MILLIS = 2000

@Composable
fun PokemonDetailScreen(
    likeEntryViewModel: LikeEntryViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClickEvolution: (String) -> Unit = {},
    onClickBackButton: () -> Unit
) {
    PokemonDetailScreen(
        uiState = pokemonDetailViewModel.uiState,
        uiStateEvolution = pokemonDetailViewModel.uiStateEvolution,
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
    uiStateEvolution: StateFlow<EvolutionChainUiState>,
    uiEvent: Flow<PokemonDetailUiEvent?>,
    consumeEvent: (PokemonDetailUiEvent) -> Unit,
    conditionState: StateFlow<PokemonDetailScreenUiData>,
    onClickBackButton: () -> Unit,
    updateIsLike: (Boolean, Int) -> Unit,
    saveLike: suspend (LikeDetails) -> Unit,
    deleteLike: suspend (LikeDetails) -> Unit,
    checkIfRoomLike: suspend (Int) -> Unit,
    onClickEvolution: (String) -> Unit,
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiStateEvolution by uiStateEvolution.collectAsStateWithLifecycle()
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
                uiStateEvolution = uiStateEvolution,
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
    uiStateEvolution: EvolutionChainUiState,
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
                MPAndroidChart(
                    uiData = uiData,
                    modifier = modifier
                )
            }
            Column {
                MenuTitle(
                    title = "▼進化",
                    modifier = modifier
                )
                when (uiStateEvolution) {
                    // 読み込みに時間がかかることがあるため、進化系譜部分だけ別Loadingを設定
                    is EvolutionChainUiState.Loading -> {
                        EvolutionChainLoading()
                    }

                    is EvolutionChainUiState.Fetched -> {
                        EvolutionChain(
                            displayEvolution = uiData.displayEvolution,
                            onClickEvolution = onClickEvolution,
                            modifier = modifier
                                .padding(vertical = 3.dp)
                        )
                    }

                    else -> {
                        EvolutionChainLoading()
                    }
                }
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
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(pokemon.imageUri)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                loading = {
                    Box(contentAlignment = Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }
                }
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
            fontSize = 15.sp,
            modifier = modifier.padding(start = 15.dp, top = 5.dp)
        )
        TypeTag(typeList = uiData.type, modifier = modifier)

        Text(
            text = String.format(stringResource(R.string.pokemon_weight), uiData.weight),
            fontSize = 15.sp,
            modifier = modifier
                .padding(start = 15.dp, top = 5.dp)
        )
        Text(
            text = String.format(stringResource(R.string.pokemon_height), uiData.height),
            fontSize = 15.sp,
            modifier = modifier
                .padding(start = 15.dp, top = 5.dp, bottom = 10.dp)
        )
    }
}

/**
 * MPAndroidChartで能力値表示
 */
@Composable
private fun MPAndroidChart(
    uiData: PokemonDetailScreenUiData,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        val abilityColor = MaterialTheme.colorScheme.onSurfaceVariant.hashCode()
        AndroidView(
            factory = { context ->
                val chart = HorizontalBarChart(context)
                //グラフのデータを設定
                val value: ArrayList<BarEntry> = ArrayList()
                value.add(BarEntry(0f, uiData.hp.toFloat())) // HP
                value.add(BarEntry(1f, uiData.attack.toFloat())) // 攻撃
                value.add(BarEntry(2f, uiData.defense.toFloat())) // 防御
                value.add(BarEntry(3f, uiData.speed.toFloat())) // スピード

                //chartに設定
                val dataSet = BarDataSet(
                    value,
                    context.resources.getString(R.string.mp_android_chart_label_name)
                ).apply {
                    setDrawValues(true)
                    color = Color.Green.hashCode()
                    valueTextColor = abilityColor
                    isHighlightEnabled = false                           //ハイライト表示無効
                    valueFormatter = BarValueFormatter()
                }

                chart.apply {
                    data = BarData(dataSet)                              //チャートのデータをセット
                    isEnabled = true
                    isDoubleTapToZoomEnabled = false                     //ズーム無効
                    isClickable = false                                  //タッチ無効
                    legend.isEnabled = false                             //凡例の削除
                    description.isEnabled = false                        //説明の削除
                    setDrawBorders(false)                                //チャートの境界線削除
                    animateXY(DURATION_MILLIS, DURATION_MILLIS)          //アニメーション
                    invalidate()                                         // refresh

                    // ------- x軸 -------
                    xAxis.apply {
                        setDrawAxisLine(false)                           //x軸に沿った線 (軸線) 削除
                        setDrawGridLines(false)                          //グリッド線削除
                        xOffset = 10f                                    //ラベルからの距離
                        position = XAxis.XAxisPosition.BOTTOM            //ラベルの位置設定
                        textColor = abilityColor                         //ラベルの色設定
                        textSize = 15f                                   //ラベルのテキストサイズ設定
                        labelCount = value.size                          //ラベル数設定
                        valueFormatter =                                 //ラベルフォーマット設定
                            IndexAxisValueFormatter(resources.getStringArray(R.array.ability))
                    }

                    // ------- y軸(左) -------
                    axisLeft.apply {
                        setDrawLabels(false)                             //y軸ラベルの削除
                        setDrawAxisLine(false)                           //y軸に沿った線 (軸線) 削除
                        setDrawGridLines(false)                          //グリッド線削除
                    }
                    // ------- y軸(右) -------
                    axisRight.apply {
                        isEnabled = true
                        textColor = abilityColor                         //ラベルの色設定
                        valueFormatter = BarValueFormatter()             //ラベルフォーマット設定
                    }
                }
            },
            modifier = Modifier
                .padding(end = 10.dp)
                .height(100.dp)
                .width(350.dp)
        )
    }
}

class BarValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
    }
}

/**
 * 進化系譜
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun EvolutionChain(
    displayEvolution: DisplayEvolution,
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
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val englishName = displayEvolution.basePokemonData.englishName
            val basePokemonImageUrl = String.format(
                stringResource(R.string.nextPokemonImageUrl),
                displayEvolution.basePokemonData.speciesNumber
            )

            // ベースポケモン
            EvolutionChainImage(
                isImageUrl = displayEvolution.basePokemonData.speciesNumber?.isNotEmpty() ?: false,
                imageUrl = basePokemonImageUrl,
                englishName = englishName,
                japaneseName = displayEvolution.basePokemonData.japaneseName ?: "NoName",
                onClickEvolution = onClickEvolution,
                modifier = modifier.align(Alignment.CenterVertically)
            )
            if (displayEvolution.nextPokemonData.isNotEmpty()) {
                Spacer(modifier = modifier.size(5.dp))
                Text(
                    text = "▶︎",
                    fontSize = 20.sp,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = modifier.size(5.dp))
                Column {
                    displayEvolution.nextPokemonData.forEach { parent ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val englishName = parent.nextPokemonData.englishName
                            val nextPokemonImageUrl = String.format(
                                stringResource(R.string.nextPokemonImageUrl),
                                parent.nextPokemonData.speciesNumber
                            )
                            // 進化ポケモン
                            EvolutionChainImage(
                                isImageUrl = parent.nextPokemonData.speciesNumber?.isNotEmpty()
                                    ?: false,
                                imageUrl = nextPokemonImageUrl,
                                englishName = englishName,
                                japaneseName = parent.nextPokemonData.japaneseName ?: "NoName",
                                onClickEvolution = onClickEvolution,
                                modifier = modifier.align(Alignment.CenterVertically)
                            )

                            if (parent.lastPokemonData.isEmpty()) {
                                // 何もしない
                            } else {
                                Spacer(modifier = modifier.size(5.dp))
                                Text(
                                    text = "︎︎▶︎",
                                    fontSize = 20.sp,
                                )
                                Spacer(modifier = modifier.size(5.dp))

                                Column {
                                    parent.lastPokemonData.forEach { child ->
                                        Spacer(modifier = modifier.size(10.dp))
                                        val englishName = child.englishName
                                        val lastPokemonImageUrl = String.format(
                                            stringResource(R.string.nextPokemonImageUrl),
                                            child.speciesNumber
                                        )
                                        // 最終進化ポケモン
                                        EvolutionChainImage(
                                            isImageUrl = child.speciesNumber?.isNotEmpty()
                                                ?: false,
                                            imageUrl = lastPokemonImageUrl,
                                            englishName = englishName,
                                            japaneseName = child.japaneseName ?: "NoName",
                                            onClickEvolution = onClickEvolution,
                                        )
                                    }
                                    Spacer(modifier = modifier.size(10.dp))
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
 * 進化系譜読み込み中画面
 */
@Composable
private fun EvolutionChainLoading(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(vertical = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.evolution_chain_loading_text),
                    modifier = modifier.padding(top = 5.dp)
                )
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
    englishName: String?,
    japaneseName: String,
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
                    .padding(bottom = 10.dp)
                    .clickable {
                        // 名前検索
                        englishName?.let { name ->
                            onClickEvolution.invoke(name)
                        }
                    },
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            AutoSizeableText(
                text = japaneseName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxTextSize = 15,
                minTextSize = 10,
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
        AutoSizeableText(
            text = japaneseName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxTextSize = 15,
            minTextSize = 10,
        )
    }
}

/**
 * 進化系譜サンプルデータ TODO API繋ぎ込んだら削除　完成するまでは一旦残す
 */
fun createEvolutionChain(): EvolutionChain {
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
fun EvolutionChain.convertToShowEvolution(): ShowEvolution {
    // ベースポケモンの情報
    val basePokemonSpeciesNumber =
        this.chain?.basePokemon?.let { Uri.parse(it.url).lastPathSegment }

    // 進化系ポケモンリスト
    val evolutionList = this.chain?.evolves?.mapNotNull { nextEvolve ->
        val nextPokemonName = nextEvolve.nextGeneration?.name
        val nextPokemonSpeciesNumber = Uri.parse(nextEvolve.nextGeneration?.url).lastPathSegment
        val lastPokemonName = nextEvolve.evolves?.mapNotNull { lastEvolve ->
            lastEvolve.lastGeneration.name
        }
        val lastPokemonSpeciesNumber =
            nextEvolve.evolves?.mapNotNull { lastEvolve ->
                Uri.parse(lastEvolve.lastGeneration.url).lastPathSegment
            }
        if (!nextPokemonSpeciesNumber.isNullOrEmpty() || !lastPokemonSpeciesNumber.isNullOrEmpty()) {
            Evolution(
                nextPokemonName = nextPokemonName,
                nextPokemonSpeciesNumber = nextPokemonSpeciesNumber,
                lastPokemonName = lastPokemonName,
                lastPokemonSpeciesNumber = lastPokemonSpeciesNumber,
            )
        } else {
            null
        }
    }

    return ShowEvolution(
        basePokemonName = this.chain?.basePokemon?.name ?: "",
        basePokemonSpeciesNumber = basePokemonSpeciesNumber,
        evolution = evolutionList
    )
}

/**
 * APIデータを使いやすいよう変換
 */
data class ShowEvolution(
    val basePokemonName: String? = "",
    val basePokemonSpeciesNumber: String? = "",
    val evolution: List<Evolution>? = emptyList()
)

data class Evolution(
    val nextPokemonName: String? = "",
    val nextPokemonSpeciesNumber: String? = "",
    val lastPokemonName: List<String>? = emptyList(),
    val lastPokemonSpeciesNumber: List<String>? = emptyList(),
)
