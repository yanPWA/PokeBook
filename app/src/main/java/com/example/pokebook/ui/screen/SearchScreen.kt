package com.example.pokebook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R

@Composable
fun SearchScreen(
    onClickType: (String) -> Unit,
    onClickSearch: (String) -> Unit,
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
        SearchType(onClickType = onClickType)
        SearchName(
            value = valueName,
            onValueChange = { valueName = it },
            modifier = modifier
        )
        SearchNumber(
            value = valueNumber,
            onValueChange = { valueNumber = it },
            onClickSearch = onClickSearch,
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
                            color = when (item) {
                                TypeName.FIGHTING.jaTypeName -> Color(color = 0xFFEE6969)
                                TypeName.POISON.jaTypeName -> Color(color = 0xFFAB7ACA)
                                TypeName.GROUND.jaTypeName -> Color(color = 0xFFC8A841)
                                TypeName.FLYING.jaTypeName -> Color(color = 0xFF64A7F1)
                                TypeName.PSYCHIC.jaTypeName -> Color(color = 0xFF9AC30E)
                                TypeName.BUG.jaTypeName -> Color(color = 0xFF51CB5A)
                                TypeName.ROCK.jaTypeName -> Color(color = 0xFFFAC727)
                                TypeName.GHOST.jaTypeName -> Color(color = 0xFF756EB4)
                                TypeName.DRAGON.jaTypeName -> Color(color = 0xFF9AC30E)
                                TypeName.DARK.jaTypeName -> Color(color = 0xFFFF8859)
                                TypeName.STEEL.jaTypeName -> Color(color = 0xFF818AA4)
                                TypeName.FAIRY.jaTypeName -> Color(color = 0xFFFC7799)
                                TypeName.FIRE.jaTypeName -> Color(color = 0xFFFFA766)
                                TypeName.WATER.jaTypeName -> Color(color = 0xFF64C5F7)
                                TypeName.ELECTRIC.jaTypeName -> Color(color = 0xFFE7D400)
                                TypeName.GRASS.jaTypeName -> Color(color = 0xFF9AC30E)
                                TypeName.SHADOW.jaTypeName -> Color(color = 0xFF333333)
                                TypeName.ICE.jaTypeName -> Color(color = 0xFF60E9F5)
                                else -> Color(color = 0xFFAEAEAE) //NORMAL,UNKNOWN
                            },
                            shape = RoundedCornerShape(5.dp),
                        )
                        .padding(2.dp)
                        .clickable { onClickType.invoke(item) }
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
                onClick = {
                    // TODO 名前で検索
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


/**
 * 図鑑No.検索
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchNumber(
    value: String,
    onValueChange: (String) -> Unit,
    onClickSearch: (String) -> Unit,
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
                onClick = { onClickSearch.invoke(value) },
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

@Preview(showBackground = true)
@Composable
private fun SearchTypePreview() {
    SearchType({})
}

@Preview(showBackground = true)
@Composable
private fun SearchNamePreview() {
    SearchName("ピカチュウ", {})
}

@Preview(showBackground = true)
@Composable
private fun SearchNumberPreview() {
    SearchNumber("25", {}, {})
}

