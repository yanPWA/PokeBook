package com.example.pokebook.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokebook.ui.viewModel.PokemonUiData
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    uiState: StateFlow<List<PokemonUiData>>,
    modifier: Modifier = Modifier,
    //    apiState: ApiState
) {
    val state by uiState.collectAsState(emptyList())
    PokeList(state)
//    when (apiState) {
//        ApiState.Error -> ErrorScreen(modifier)
//        ApiState.Loading -> LoadingScreen(modifier)
//        is ApiState.Success<*> -> PokeList(state, modifier)
//    }
}

@Composable
private fun PokeList(
    pokeList: List<PokemonUiData>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp)
    ) {
        items(pokeList) { listItem ->
            PokeCard(listItem)
        }
    }
}

@Composable
private fun PokeCard(pokemon: PokemonUiData, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp), elevation = cardElevation(4.dp)) {
        Box(contentAlignment = Alignment.BottomCenter) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(pokemon.imageUri).apply { Log.d("test","pokemon.imageUri:${pokemon.imageUri}") }
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
                    .padding(bottom = 2.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(3.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PokeCardPreview() {
    PokeCard(
        PokemonUiData(name = "ピカチュウ")
    )
}

@Preview
@Composable
private fun PokeListPreview() {
    PokeList(
        listOf(
            PokemonUiData(name = "ピカチュウ"),
            PokemonUiData(name = "ピカチュウ"),
            PokemonUiData(name = "ピカチュウ"),
            PokemonUiData(name = "ピカチュウ"),
            PokemonUiData(name = "ピカチュウ"),
            PokemonUiData(name = "ピカチュウ")
        )
    )
}
