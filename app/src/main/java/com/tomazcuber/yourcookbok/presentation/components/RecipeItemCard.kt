package com.tomazcuber.yourcookbok.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme

@Composable
fun RecipeItemCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = onFavoriteClick
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    contentDescription = "Toggle Favorite",

                )
            }
        }
    }
}

// --- Previews ---

// Helper function to create a dummy recipe for previews
private fun getPreviewRecipe(
    name: String = "Creamy Tomato Pasta"
) = Recipe(
    id = "1",
    name = name,
    imageUrl = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg", // Using a real URL
    instructions = "...",
    ingredients = emptyList(),
    category = "Pasta",
    area = "Italian",
)

@Preview(showBackground = true, name = "Default State")
@Composable
fun RecipeItemCardPreview() {
    // Wrap the preview in your app's theme
    YourCookbokTheme {
        RecipeItemCard(
            recipe = getPreviewRecipe(),
            isFavorite = false,
            onClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorited State")
@Composable
fun RecipeItemCardFavoritedPreview() {
    YourCookbokTheme {
        RecipeItemCard(
            recipe = getPreviewRecipe("Spicy Peanut Noodles with extra long text that will overflow"),
            isFavorite = true,
            onClick = {},
            onFavoriteClick = {}
        )
    }
}