package com.example.pokebook.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * ボトムナビゲーション
 */
sealed class BottomNavItems(
    val route: String,
    val name: String,
    val icon: ImageVector
) {
    object Home : BottomNavItems("home", "HOME", Icons.Filled.Home)
    object Search : BottomNavItems("search", "SEARCH", Icons.Filled.Search)
    object Like : BottomNavItems("like", "LIKE", Icons.Filled.Star)
    object Setting : BottomNavItems("setting", "SETTING", Icons.Filled.Settings)
}

val navItems = listOf(
    BottomNavItems.Home,
    BottomNavItems.Search,
    BottomNavItems.Like,
    BottomNavItems.Setting
)