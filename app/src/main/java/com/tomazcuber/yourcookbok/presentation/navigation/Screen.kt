package com.tomazcuber.yourcookbok.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface AppDestination {
    val route: String
    val label: String
    val icon: ImageVector
}

@Serializable
object SearchScreen : AppDestination {
    override val route = "search"
    override val label = "Search"
    override val icon = Icons.Default.Search
}

@Serializable
object SavedScreen : AppDestination {
    override val route = "saved"
    override val label = "Saved"
    override val icon = Icons.Default.Favorite
}