package com.tomazcuber.yourcookbok.search.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.presentation.components.RecipeItemCard
import com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme
import com.tomazcuber.yourcookbok.search.SearchViewModel

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(SearchEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextField(
                value = state.searchQuery,
                onValueChange = { onEvent(SearchEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape),
                placeholder = { Text("Search for a recipe...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onEvent(SearchEvent.OnSearchQueryChanged("")) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onEvent(SearchEvent.OnSearchSubmit)
                        keyboardController?.hide()
                    }
                ),
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else if (state.recipes.isEmpty()) {
                    Text(
                        text = "Find your next favorite recipe",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.recipes) { recipeModel ->
                            RecipeItemCard(
                                recipe = recipeModel.recipe,
                                isFavorite = recipeModel.isSaved,
                                onClick = { onEvent(SearchEvent.OnRecipeClick(recipeModel.recipe)) },
                                onFavoriteClick = { onEvent(SearchEvent.OnToggleSave(recipeModel.recipe)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Previews ---

private fun getPreviewRecipes(): List<RecipeUiModel> {
    val recipe1 = Recipe(
        id = "1",
        name = "Chicken Karaage",
        imageUrl = "https://www.themealdb.com/images/media/meals/tyywsw1505930373.jpg",
        instructions = "", ingredients = emptyList(), category = "", area = ""
    )
    val recipe2 = Recipe(
        id = "2",
        name = "Chicken and Mushroom Pie",
        imageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg",
        instructions = "", ingredients = emptyList(), category = "", area = ""
    )
    return listOf(
        RecipeUiModel(recipe = recipe1, isSaved = true),
        RecipeUiModel(recipe = recipe2, isSaved = false)
    )
}

@Preview(name = "Empty State", showBackground = true)
@Composable
fun SearchScreenEmptyPreview() {
    YourCookbokTheme {
        SearchScreen(state = SearchUiState(), onEvent = {})
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun SearchScreenLoadingPreview() {
    YourCookbokTheme {
        SearchScreen(state = SearchUiState(isLoading = true), onEvent = {})
    }
}

@Preview(name = "With Results", showBackground = true)
@Composable
fun SearchScreenWithResultsPreview() {
    YourCookbokTheme {
        SearchScreen(
            state = SearchUiState(
                searchQuery = "Chicken",
                recipes = getPreviewRecipes()
            ),
            onEvent = {}
        )
    }
}