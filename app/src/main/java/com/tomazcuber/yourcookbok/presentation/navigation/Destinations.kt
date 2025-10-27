package com.tomazcuber.yourcookbok.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class AppDestination(
    @Transient
    val label: String? = null,
    @Transient
    val icon: ImageVector? = null
) {

    @Serializable
    data object Search : AppDestination(
        label = "Search",
        icon = Icons.Default.Search
    )


    @Serializable
    data object Saved : AppDestination(
        label = "Saved",
        icon = Icons.Default.Favorite
    )

    @Serializable
    data class RecipeDetail(val recipeId: String) : AppDestination()
}

val BOTTOM_BAR_DESTINATIONS = listOf(
    AppDestination.Search,
    AppDestination.Saved
)