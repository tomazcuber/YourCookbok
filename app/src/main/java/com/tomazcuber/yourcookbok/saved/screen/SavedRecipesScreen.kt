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
import androidx.compose.ui.tooling.preview.Preview
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
        onEvent = viewModel::onEvent
    )
}

@Composable
fun SavedRecipesScreen(
    recipes: List<Recipe>,
    onEvent: (SavedRecipesEvent) -> Unit
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
                    onClick = { onEvent(SavedRecipesEvent.OnRecipeClick(recipe)) },
                    onFavoriteClick = { onEvent(SavedRecipesEvent.OnUnsaveClick(recipe)) }
                )
            }
        }
    }
}

// --- Previews ---

private fun getPreviewRecipes(): List<Recipe> {
    return listOf(
        Recipe(
            id = "1",
            name = "Spicy Arrabiata Penne",
            imageUrl = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
            instructions = "", ingredients = emptyList(), category = "", area = ""
        ),
        Recipe(
            id = "2",
            name = "Chicken and Mushroom Pie",
            imageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg",
            instructions = "", ingredients = emptyList(), category = "", area = ""
        )
    )
}

@Preview(name = "Empty State", showBackground = true)
@Composable
fun SavedRecipesScreenEmptyPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        SavedRecipesScreen(
            recipes = emptyList(),
            onEvent = {}
        )
    }
}

@Preview(name = "With Content", showBackground = true)
@Composable
fun SavedRecipesScreenWithContentPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        SavedRecipesScreen(
            recipes = getPreviewRecipes(),
            onEvent = {}
        )
    }
}