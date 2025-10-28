package com.tomazcuber.yourcookbok.detail.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tomazcuber.yourcookbok.detail.viewmodel.RecipeDetailViewModel
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.presentation.components.ShimmerPlaceholder

@Composable
fun RecipeDetailRoute(
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    RecipeDetailScreen(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    state: RecipeDetailUiState,
    onEvent: (RecipeDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(RecipeDetailEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(
                    text = state.recipe?.name ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.recipe != null) {
                        IconButton(onClick = { onEvent(RecipeDetailEvent.OnToggleSave) }) {
                            Icon(
                                imageVector = if (state.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle Favorite"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        {
            if (state.isLoading) {
                RecipeDetailPlaceholder()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val recipe = state.recipe ?: return@LazyColumn
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column {
                                AsyncImage(
                                    model = recipe.imageUrl,
                                    contentDescription = recipe.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )

                                IngredientColumn(
                                    ingredients = recipe.ingredients,
                                )
                            }
                        }
                    }

                    item {
                        InstructionCard(
                            instructions = state.numberedInstructions,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun RecipeDetailPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Approximate height for an image
            )

            Column(
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Title
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // 50% width
                        .height(30.dp)
                )
                // Lines for ingredients
                Spacer(Modifier.height(16.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(Modifier.height(8.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(Modifier.height(8.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Title
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // 50% width
                        .height(30.dp)
                        .padding(top = 16.dp)
                )
                // Lines for instructions
                Spacer(Modifier.height(16.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
                Spacer(Modifier.height(8.dp))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }
    }
}

@Composable
private fun IngredientColumn(
    ingredients: List<Ingredient>,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ingredients.forEach { ingredient ->
            Text(
                text = "${ingredient.measure} ${ingredient.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

@Composable
private fun InstructionCard(
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            instructions.forEach{ step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

// --- Previews ---
@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(
                recipe = Recipe(
                    id = "1",
                    name = "Spicy Arrabiata Penne",
                    imageUrl = "",
                    instructions = "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\\r\\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\\r\\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.",
                    ingredients = listOf(
                        Ingredient("penne rigate", "1 pound"),
                        Ingredient("olive oil", "1/4 cup"),
                        Ingredient("garlic", "3 cloves"),
                        Ingredient("chopped tomatoes", "1 tin"),
                        Ingredient("red chilli flakes", "1/2 teaspoon"),
                        Ingredient("italian seasoning", "1/2 teaspoon"),
                        Ingredient("basil", "6 leaves"),
                        Ingredient("Parmigiano-Reggiano", "sprinkling"),
                        ),
                    category = "Vegetarian",
                    area = "Italian"
                ),
                numberedInstructions = listOf(
                    "1. Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.",
                    "2. In a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.",
                    "3. Drain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm."
                ),
                isLoading = false,
                isSaved = true
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenLoadingPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(
                recipe = Recipe(
                    id = "1",
                    name = "Spicy Arrabiata Penne",
                    imageUrl = "",
                    instructions = "Some instructions.",
                    ingredients = emptyList(),
                    category = "Vegetarian",
                    area = "Italian"
                ),
                isLoading = true,
                isSaved = true
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenErrorPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(
                recipe = null,
                isLoading = false,
                isSaved = true
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}