package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

/**
 * エラー画面
 */
@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.loading_failed))
    }
}

/**
 * 文字サイズの自動調整
 */
@Composable
fun AutoSizeableText(
    text: String,
    color: Color,
    maxTextSize: Int = 50,
    minTextSize: Int = 40,
    modifier: Modifier
) {
    var textSize by remember { mutableStateOf(maxTextSize) }
    val checked = remember(text) { mutableMapOf<Int, Boolean?>() }
    var overflow by remember { mutableStateOf(TextOverflow.Clip) }

    Text(
        text = text,
        color = color,
        fontSize = textSize.sp,
        maxLines = 1,
        overflow = overflow,
        modifier = modifier,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                checked[textSize] = true
                if (textSize > minTextSize) {
                    textSize -= 1
                } else {
                    overflow = TextOverflow.Ellipsis
                }
            } else {
                checked[textSize] = false
                if (textSize < maxTextSize) {
                    if (checked[textSize + 1] == null) {
                        textSize += 1
                    }
                }
            }
        }
    )
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
            text = "該当するポケモンが存在しません",
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
                text = "検索TOPに戻る",
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}