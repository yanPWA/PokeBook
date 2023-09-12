package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData
import com.example.pokebook.ui.viewModel.Search.SearchConditionState
import com.example.pokebook.ui.viewModel.Search.SearchUiEvent
import com.example.pokebook.ui.viewModel.Search.SearchUiState
import com.example.pokebook.ui.viewModel.Search.SearchViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun SearchListScreen(
    searchViewModel: SearchViewModel,
    onClickCard: (Int, Int) -> Unit,
    onClickBackSearchScreen: () -> Unit
) {
    SearchListScreen(
        uiState = searchViewModel.uiState,
        uiStateEvent = searchViewModel.uiEvent,
        consumeEvent = searchViewModel::processed,
        conditionState = searchViewModel.conditionState,
        onClickBack = searchViewModel::onClickBack,
        onClickNext = searchViewModel::onClickNext,
        onClickCard = onClickCard,
        updateButtonStates = searchViewModel::updateButtonStates,
        updateIsFirst = searchViewModel::updateIsFirst,
        onClickBackSearchScreen = onClickBackSearchScreen,
        onClickBackButton = onClickBackSearchScreen
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun SearchListScreen(
    uiState: StateFlow<SearchUiState>,
    uiStateEvent: Flow<SearchUiEvent?>,
    consumeEvent: (SearchUiEvent) -> Unit,
    conditionState: StateFlow<SearchConditionState>,
    onClickBack: () -> Unit,
    onClickNext: () -> Unit,
    onClickCard: (Int, Int) -> Unit,
    updateButtonStates: (Boolean, Boolean) -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    onClickBackSearchScreen: () -> Unit,
    onClickBackButton: () -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val uiEvent by uiStateEvent.collectAsStateWithLifecycle(initialValue = null)
    val searchWord = conditionState.value.pokemonTypeName
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    when (uiEvent) {
        is SearchUiEvent.Error -> {
            ErrorScreen(
                consumeEvent = consumeEvent,
                event = uiEvent as SearchUiEvent.Error
            )
        }

        null -> {}
    }

    when (state) {
        is SearchUiState.Fetched -> {
            if ((state as SearchUiState.Fetched).searchList.isEmpty()) {
                PokemonNotFound(
                    onClickBackSearchScreen = onClickBackSearchScreen
                )
            } else {
                SearchListScreen(
                    pokemonUiDataList = (state as SearchUiState.Fetched).searchList,
                    isFirst = conditionState.value.isFirst,
                    searchWord = searchWord,
                    pagePosition = conditionState.value.pagePosition,
                    maxPage = conditionState.value.maxPage,
                    onClickBack = onClickBack,
                    onClickNext = onClickNext,
                    onClickCard = onClickCard,
                    updateButtonStates = updateButtonStates,
                    updateIsFirst = updateIsFirst,
                    onClickBackSearchScreen = onClickBackSearchScreen,
                    lazyGridState = lazyGridState,
                    coroutineScope = coroutineScope
                )
            }
        }

        SearchUiState.Loading -> {
            Column {
                DefaultHeader(
                    pagePosition = conditionState.value.pagePosition,
                    title = String.format(
                        stringResource(R.string.header_title_search_list_1),
                        searchWord
                    ) + stringResource(id = R.string.header_title_search_list_loading),
                    onClickBackButton = onClickBackButton
                )
                LoadingScreen()
            }
        }

        SearchUiState.ResultError -> {
            ResultError(
                text = stringResource(R.string.result_error_text),
                onClickBackSearchScreen = onClickBackSearchScreen
            )
        }

        else -> {}
    }
}

@Composable
private fun SearchListScreen(
    pokemonUiDataList: ImmutableList<PokemonListUiData>,
    isFirst: Boolean,
    searchWord: String,
    pagePosition: Int,
    maxPage: String,
    onClickBack: () -> Unit,
    onClickNext: () -> Unit,
    onClickCard: (Int, Int) -> Unit,
    updateButtonStates: (Boolean, Boolean) -> Unit,
    updateIsFirst: (Boolean) -> Unit,
    onClickBackSearchScreen: () -> Unit,
    lazyGridState: LazyGridState,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        DefaultHeader(
            pagePosition = pagePosition,
            title = String.format(
                stringResource(R.string.header_title_search_list_1),
                searchWord
            ) + if (maxPage.isEmpty()) {
                stringResource(id = R.string.header_title_search_list_loading)
            } else {
                String.format(
                    stringResource(R.string.header_title_search_list_2),
                    pagePosition.plus(1),
                    maxPage
                )
            },
            maxPage = maxPage,
            updateButtonStates = updateButtonStates,
            onClickBack = onClickBack,
            onClickNext = onClickNext,
            onClickBackButton = onClickBackSearchScreen,
            isSearch = true
        )
        PokeTypeList(
            pokemonUiDataList = pokemonUiDataList,
            isFirst = isFirst,
            onClickCard = onClickCard,
            updateIsFirst = updateIsFirst,
            lazyGridState = lazyGridState,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun PokeTypeList(
    pokemonUiDataList: ImmutableList<PokemonListUiData>,
    isFirst: Boolean,
    onClickCard: (Int, Int) -> Unit,
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
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(pokemonUiDataList.toMutableList()) { listItem ->
            PokeTypeCard(
                pokemon = listItem,
                onClickCard = onClickCard,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokeTypeCard(
    pokemon: PokemonListUiData,
    onClickCard: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onClickCard.invoke(pokemon.speciesNumber?.toInt() ?: 0, pokemon.pokemonNumber) }
    ) {
        if (!pokemon.imageUrl.isNullOrEmpty()) {
            Box(
                contentAlignment = Alignment.BottomCenter
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
                    contentDescription = null
                )
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
}
