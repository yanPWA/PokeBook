package com.example.pokebook.ui.screen.common

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 文字サイズの自動調整
 */
@Composable
fun AutoSizeableText(
    text: String,
    color: Color,
    maxTextSize: Int = 50,
    minTextSize: Int = 40,
    modifier: Modifier = Modifier
) {
    var textSize by remember { mutableStateOf(maxTextSize) }
    val checked = remember(text) { mutableMapOf<Int, Boolean?>() }
    var overflow by remember { mutableStateOf(TextOverflow.Clip) }

    androidx.compose.material.Text(
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