package com.tomazcuber.yourcookbok.saved.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.presentation.components.RecipeItemCard
import com.tomazcuber.yourcookbok.saved.viewmodel.SavedRecipesViewModel

@Composable
fun SavedRecipesRoute(
    viewModel: SavedRecipesViewModel = hiltViewModel()
) {
    val savedRecipes by viewModel.savedRecipes.collectAsState()

    SavedRecipesScreen(
        recipes = savedRecipes,
        onRecipeClick = { /* TODO: Handle recipe click */ },
        onFavoriteClick = { /* TODO: Handle favorite click */ }
    )
}

@Composable
fun SavedRecipesScreen(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onFavoriteClick: (Recipe) -> Unit
) {
    if (recipes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You haven't saved any recipes yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipes) { recipe ->
                RecipeItemCard(
                    recipe = recipe,
                    isFavorite = true, // All recipes on this screen are favorites
                    onClick = { onRecipeClick(recipe) },
                    onFavoriteClick = { onFavoriteClick(recipe) }
                )
            }
        }
    }
}