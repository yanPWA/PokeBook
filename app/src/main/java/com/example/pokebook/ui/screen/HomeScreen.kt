package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R
import com.example.pokebook.data.PokemonDataSource
import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.Profile

@Composable
fun HomeScreen() {
        PokeList(PokemonDataSource.pokemonList)
}

@Composable
private fun PokeList(pokeList:List<Profile>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp)
    ){
        items(pokeList){ listItem ->
            PokeCard(listItem)
        }
    }
}

@Composable
private fun PokeCard(pokemon: Profile, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp), elevation = cardElevation(4.dp)) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Image(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                painter = painterResource(id= pokemon.imageResourceId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
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
    PokeCard(Profile( name = "pika1",imageResourceId = R.drawable.poke))
}

@Preview
@Composable
private fun HomeScreenPreview(){
    HomeScreen()
}