package com.tomazcuber.yourcookbok.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Saved : Screen("saved", "Saved", Icons.Default.Favorite)
}