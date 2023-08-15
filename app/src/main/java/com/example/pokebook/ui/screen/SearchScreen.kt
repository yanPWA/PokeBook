package com.example.pokebook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R
import com.example.pokebook.ui.viewModel.Detail.PokemonDetailViewModel
import com.example.pokebook.ui.viewModel.Search.SearchViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    pokemonDetailViewModel: PokemonDetailViewModel,
    onClickSearchPokemonName: () -> Unit,
    onClickSearchPokemonNumber: () -> Unit,
    onClickSearchTypeButton: () -> Unit,
//    onClickBackButton: () -> Unit
) {
    SearchScreen(
        onClickSearchType = searchViewModel::getPokemonByType,
        onClickSearchName = searchViewModel::getPokemonByName,
        onClickSearchNumber = pokemonDetailViewModel::getPokemonSpeciesByUiData,
        onClickSearchPokemonNumber = onClickSearchPokemonNumber,
        onClickSearchTypeButton = onClickSearchTypeButton
    )
}

@Composable
private fun SearchScreen(
    onClickSearchType: (String) -> Unit,
    onClickSearchName: (String) -> Unit,
    onClickSearchNumber: (Int) -> Unit,
    onClickSearchPokemonNumber: () -> Unit,
    onClickSearchTypeButton: () -> Unit
) {
    SearchScreen(
        onClickSearchType = onClickSearchType,
        onClickSearchName = onClickSearchName,
        onClickSearchNumber = onClickSearchNumber,
        onClickSearchPokemonNumber = onClickSearchPokemonNumber,
        onClickSearchTypeButton = onClickSearchTypeButton,
        modifier = Modifier
    )
}

@Composable
private fun SearchScreen(
    onClickSearchType: (String) -> Unit,
    onClickSearchName: (String) -> Unit,
    onClickSearchNumber: (Int) -> Unit,
    onClickSearchPokemonNumber: () -> Unit,
    onClickSearchTypeButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    var valueName by remember { mutableStateOf("") }
    var valueNumber by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        SearchType(
            onClickType = onClickSearchType,
            onClickSearchTypeButton = onClickSearchTypeButton
        )
        SearchName(
            value = valueName,
            onValueChange = { valueName = it },
            onClickSearchName = onClickSearchName,
            modifier = modifier
        )
        SearchNumber(
            value = valueNumber,
            onValueChange = { valueNumber = it },
            onClickSearchNumber = onClickSearchNumber,
            onClickSearchPokemonNumber = onClickSearchPokemonNumber,
            modifier = modifier
        )
    }
}

/**
 * タイプ別検索
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchType(
    onClickType: (String) -> Unit,
    onClickSearchTypeButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val typeList = stringArrayResource(id = R.array.all_types)
        Text(
            text = stringResource(R.string.type_search_title),
            fontSize = 30.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 2.dp, top = 5.dp)
        )
        FlowRow(
            modifier = modifier
                .padding(5.dp)
        ) {
            typeList.forEach { item ->
                Text(
                    text = item,
                    fontSize = 15.sp,
                    maxLines = 2,
                    modifier = modifier
                        .padding(5.dp)
                        .background(
                            color = item.convertToColorCode(),
                            shape = RoundedCornerShape(5.dp),
                        )
                        .padding(2.dp)
                        .clickable {
                            onClickType.invoke(item.convertToTypeNumber())
                            onClickSearchTypeButton.invoke()
                        }
                )
            }
        }
    }
}


/**
 * ポケモン名検索
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchName(
    value: String,
    onValueChange: (String) -> Unit,
    onClickSearchName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(5.dp)
    ) {
        Text(
            text = stringResource(R.string.search_name_title),
            fontSize = 30.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 2.dp)
        )
        Row(
            modifier = modifier
                .padding(5.dp)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(stringResource(R.string.search_name_label)) },
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Button(
                onClick = { onClickSearchName.invoke(value) },
                modifier = modifier
                    .padding(2.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.search_button),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


/**
 * 図鑑No.検索
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchNumber(
    value: String,
    onValueChange: (String) -> Unit,
    onClickSearchNumber: (Int) -> Unit,
    onClickSearchPokemonNumber: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(5.dp)
    ) {
        Text(
            text = stringResource(R.string.search_number),
            fontSize = 30.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 2.dp)
        )
        Row(
            modifier = modifier
                .padding(5.dp)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(stringResource(R.string.search_number)) },
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    onClickSearchNumber.invoke(value.toInt())
                    onClickSearchPokemonNumber.invoke()
                },
                modifier = modifier
                    .padding(2.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.search_button),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
