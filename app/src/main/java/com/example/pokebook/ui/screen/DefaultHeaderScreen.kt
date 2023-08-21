package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R

/**
 * ポケモン一覧のヘッダー領域
 */
@Composable
fun DefaultHeader(
    title: String,
    updateButtonStates: (Boolean, Boolean) -> Unit = { _, _ -> },
    onClickNext: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickBackButton: () -> Unit = {},
    isSearch: Boolean = false
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSearch) {
            Image(
                imageVector = ImageVector.vectorResource(
                    id = if (isSystemInDarkTheme()) R.drawable.arrow_back_fillf else R.drawable.arrow_back_fill0
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Start)
                    .clickable { onClickBackButton.invoke() }
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.background),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .wrapContentHeight()
                    .clickable {
                        updateButtonStates.invoke(true, false)
                        onClickBack.invoke()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.back_button),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .weight(1F)
                    .wrapContentHeight()
                    .clickable {
                        updateButtonStates.invoke(false, true)
                        onClickNext.invoke()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.next_button),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
