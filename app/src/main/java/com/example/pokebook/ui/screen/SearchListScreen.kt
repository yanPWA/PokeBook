package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.PokemonListUiData
import com.example.pokebook.ui.viewModel.SearchConditionState
import com.example.pokebook.ui.viewModel.SearchUiState
import com.example.pokebook.ui.viewModel.SearchViewModel
import kotlinx.coroutines.flow.StateFlow


@Composable
fun SearchListScreen(
    searchViewModel: SearchViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickCard: () -> Unit,
    onClickBackButton: () -> Unit
) {
    SearchListScreen(
        uiState = searchViewModel.uiState,
        conditionState = searchViewModel.conditionState,
        onClickCard = onClickCard,
        getPokemonSpecies = pokemonDetailViewModel::getPokemonSpecies
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun SearchListScreen(
    uiState: StateFlow<SearchUiState>,
    conditionState: StateFlow<SearchConditionState>,
    onClickCard: () -> Unit,
    getPokemonSpecies: (String) -> Unit
) {
    val state by uiState.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()
    val searchWord = conditionState.value.pokemonTypeName

    when (state) {
        is SearchUiState.Fetched -> {
            SearchListScreen(
                pokemonUiDataList = (state as SearchUiState.Fetched).searchList,
                searchWord = searchWord,
                onClickCard = onClickCard,
                getPokemonSpecies = getPokemonSpecies,
                lazyGridState = lazyGridState
            )
        }

        SearchUiState.Loading -> {
            Column {
                DefaultHeader(
                    isSearchListScreen = true,
                    searchWord = searchWord
                )
                LoadingScreen()
            }
        }

        else -> {}
    }
}

@Composable
private fun SearchListScreen(
    pokemonUiDataList: List<PokemonListUiData>,
    searchWord: String,
    onClickCard: () -> Unit,
    getPokemonSpecies: (String) -> Unit,
    lazyGridState: LazyGridState
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        DefaultHeader(
            isSearchListScreen = true,
            searchWord = searchWord
        )
        PokeTypeList(
            pokemonUiDataList = pokemonUiDataList,
            onClickCard = onClickCard,
            getPokemonSpecies = getPokemonSpecies,
            lazyGridState = lazyGridState
        )
    }
}

@Composable
private fun PokeTypeList(
    pokemonUiDataList: List<PokemonListUiData>,
    onClickCard: () -> Unit,
    getPokemonSpecies: (String) -> Unit,
    lazyGridState: LazyGridState
){
    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(minSize = 150.dp),
    ) {
        items(pokemonUiDataList) { listItem ->
            PokeTypeCard(
                pokemon = listItem,
                onClickCard = onClickCard,
                getPokemonSpecies = getPokemonSpecies
            )
        }
        // 下部がボトムナビゲーションとかぶってしまった為
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokeTypeCard(
    pokemon: PokemonListUiData,
    onClickCard: () -> Unit,
    getPokemonSpecies: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            getPokemonSpecies.invoke(pokemon.id)
            onClickCard.invoke()
        }
    ) {
        Text(
            text = String.format(
                stringResource(R.string.pokemon_name),
                pokemon.id,
                pokemon.name
            ),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .fillMaxWidth()
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

