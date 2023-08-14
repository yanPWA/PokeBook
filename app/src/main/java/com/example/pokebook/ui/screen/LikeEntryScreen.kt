package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.AppViewModelProvider

import com.example.pokebook.ui.viewModel.Like.LikeDetails
import com.example.pokebook.ui.viewModel.Like.LikeEntryViewModel
import com.example.pokebook.ui.viewModel.Like.LikeUiState
import com.example.pokebook.ui.viewModel.Like.PokemonListUiData
import com.example.pokebook.ui.viewModel.Search.SearchUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ここでやりたいこと
 * DBに保存されているデータを表示すること
 * 一覧画面はホームタブと同じ感じ
 * Likeの登録削除ボタンを実装
 * 詳細画面表示
 * 戻るボタン
 *
 */
@Composable
fun LikeEntryScreen(
    onClickCard: () -> Unit,
    onClickBackButton: () -> Unit,
    likeEntryViewModel: LikeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LikeEntryBody(
        likeEntryViewModel = likeEntryViewModel,
        uiState = likeEntryViewModel.uiState,
        onLikeValueChange = likeEntryViewModel::updateLikeListUiState,
        onClickCard = onClickCard,
        onClickBackButton = onClickBackButton
    )
}

@Composable
private fun LikeEntryBody(
    likeEntryViewModel: LikeEntryViewModel,
    uiState: StateFlow<LikeUiState>,
    onLikeValueChange: (LikeDetails) -> Unit,
    onClickCard: () -> Unit,
    onClickBackButton: () -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    LikeScreen(
        likeEntryViewModel = likeEntryViewModel,
//        likeList = (state as LikeUiState.Fetched).uiDataList,
        likeList = createDummyList(),
        onClickCard = onClickCard,
        onClickBackButton = onClickBackButton
    )
//    when (state) {
//        is LikeUiState.Fetched -> {
//            if ((state as LikeUiState.Fetched).uiDataList.isEmpty()) {
//                PokemonNotFound(
//                    onClickBackSearchScreen = onClickBackButton
//                )
//            } else {
//                LikeScreen(
//                    likeEntryViewModel = likeEntryViewModel,
//                    likeList = (state as LikeUiState.Fetched).uiDataList,
//                    onClickCard = onClickCard,
//                    onClickBackButton = onClickBackButton
//                )
//            }
//        }
//
//        else -> {
//        }
//    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun LikeScreen(
    likeEntryViewModel: LikeEntryViewModel,
    likeList: MutableList<PokemonListUiData>,
//    uiEvent: Flow<LikeUiEvent>
//    consumeEvent: (LikeUiEvent) -> Unit,
    onClickCard: () -> Unit,
    onClickBackButton: () -> Unit,
//    onClickRetryGetList: (Boolean) -> Unit
) {
//    val uiEvent by uiEvent.collectAsStateWithLifecycle(initialValue = null)
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LikeScreen(
        likeEntryViewModel = likeEntryViewModel,
        likeList = likeList,
        onClickCard = onClickCard,
        onClickBackButton = onClickBackButton,
        lazyGridState = lazyGridState,
        coroutineScope = coroutineScope
    )
}

@Composable
fun LikeScreen(
    likeEntryViewModel: LikeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    likeList: MutableList<PokemonListUiData>,
    onClickCard: () -> Unit,
    onClickBackButton: () -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LikeHeader(
            title = "お気に入りのポケモン一覧",
            onClickBackButton = onClickBackButton
        )

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(likeList) { listItem ->
                LikePokeCard(
                    likeEntryViewModel = likeEntryViewModel,
                    pokemon = listItem,
                    onClickCard = onClickCard,
                    coroutineScope = coroutineScope
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikePokeCard(
    likeEntryViewModel: LikeEntryViewModel,
    pokemon: PokemonListUiData,
    onClickCard: () -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClickCard,
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
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .background(pokemon.type.convertToColorCodeByTypeName())
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.favorite_fill0_wght400_grad0_opsz48
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(30.dp)
                            .clickable {
                                if (pokemon.isLike) {
                                    coroutineScope.launch {
                                        likeEntryViewModel.deleteLike()
                                    }
                                } else {
                                    coroutineScope.launch {
                                        likeEntryViewModel.saveLike()
                                    }
                                }
                            }
                    )
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

fun createDummyList(): MutableList<PokemonListUiData> {
    val list = mutableListOf<PokemonListUiData>()
    for (i in 0 until 11) {
        list.add(
            PokemonListUiData(
                id = i,
                name = "pika",
                displayName = "ピカチュウ",
                imageUrl = "",
                isLike = true,
                type = "electric"
            )
        )
    }
    return list
}

@Preview(showBackground = true)
@Composable
fun LikeScreenPreview() {
    LikeScreen(
        likeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
        likeList = createDummyList(),
        onClickCard = {},
        onClickBackButton = {},
        lazyGridState = LazyGridState(),
        coroutineScope = rememberCoroutineScope()
    )
}

@Preview(showBackground = true)
@Composable
fun LikePokeCardPreview() {
    LikePokeCard(
        likeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
        pokemon = PokemonListUiData(
            id = 1,
            name = "pika",
            displayName = "ピカチュウ",
            imageUrl = "",
            isLike = true,
            type = "electric"
        ),
        onClickCard = {},
        coroutineScope = rememberCoroutineScope()
    )
}

/**
 * ヘッダー部分
 */
@Composable
fun LikeHeader(
    title: String,
    onClickBackButton: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ImageVector.vectorResource(
                id = if (isSystemInDarkTheme()) R.drawable.arrow_back_fillf else R.drawable.arrow_back_fill0
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onClickBackButton.invoke() }
                .size(30.dp)
                .padding(5.dp)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.background),
            textAlign = TextAlign.Center
        )
    }
}
