package com.example.pokebook.ui.screen

import android.annotation.SuppressLint
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokebook.R

sealed class Item(var dist: String, var icon: ImageVector){
    object Home: Item("home", Icons.Filled.Home)
    object Search: Item("search", Icons.Filled.Search)
    object Like: Item("like", Icons.Filled.Star)
    object Setting: Item("setting", Icons.Filled.Settings)
}

@Composable
fun MultipleItemsBottomNavigation() {
    var selectedItem = remember { mutableStateOf(0) }
    val items = listOf(Item.Home, Item.Search, Item.Like, Item.Setting)
    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.dist) },
                label = { Text(item.dist) },
                alwaysShowLabel = false, // 4つ以上のItemのとき
                selected = selectedItem.value == index,
                onClick = { selectedItem.value = index }
            )
        }
    }
}
/**
 * ステータスバーの背景色を変更
 */
@Composable
fun StatusBarColorSample() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color(0xFF0057CC))
    }
}
