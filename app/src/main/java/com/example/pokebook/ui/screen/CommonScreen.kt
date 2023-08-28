package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R

/**
 * Loading画面
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

/**
 * エラー画面
 */
@Composable
fun <T> ErrorScreen(
    consumeEvent: (T) -> Unit,
    event: T,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        AlertDialog(
            onDismissRequest = { consumeEvent.invoke(event) },
            confirmButton = {
                TextButton(
                    onClick = { consumeEvent.invoke(event) }
                ) {
                    Text(text = "閉じる")
                }
            },
            text = {
                Text("ポケモン取得エラー")
            }
        )
    }
}

/**
 * 該当するポケモンがいない
 */
@Composable
fun PokemonNotFound(
    onClickBackSearchScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.pokemon_not_found_text),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
        Button(
            onClick = onClickBackSearchScreen,
            modifier = Modifier
                .padding(top = 30.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = stringResource(R.string.back_screen_button_text),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * ホームタブ取得エラー画面
 */
@Composable
fun HomeResultError(
    onClickRetryGetList: () -> Unit,
    isFirst: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Image(
            imageVector = ImageVector.vectorResource(
                id =
                if (isSystemInDarkTheme()) {
                    R.drawable.baseline_warning_amber_24_fillf
                } else {
                    R.drawable.baseline_warning_amber_24_fill0
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
        Text(
            text = stringResource(R.string.result_error_text),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
        Button(
            onClick = { onClickRetryGetList.invoke() },
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = stringResource(R.string.retry_button_text),
                modifier = Modifier
                    .wrapContentWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 取得エラー画面
 */
@Composable
fun ResultError(
    text:String,
    onClickBackSearchScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Image(
            imageVector = ImageVector.vectorResource(
                id =
                if (isSystemInDarkTheme()) {
                    R.drawable.baseline_warning_amber_24_fillf
                } else {
                    R.drawable.baseline_warning_amber_24_fill0
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
        Text(
            text = text,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
        Button(
            onClick = onClickBackSearchScreen,
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = stringResource(R.string.back_screen_button_text),
                modifier = Modifier
                    .wrapContentWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
