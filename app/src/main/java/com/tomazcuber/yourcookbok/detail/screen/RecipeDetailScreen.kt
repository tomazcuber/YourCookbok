package com.tomazcuber.yourcookbok.detail.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomazcuber.yourcookbok.detail.viewmodel.RecipeDetailViewModel

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
    Scaffold(
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
                    IconButton(onClick = { onEvent(RecipeDetailEvent.OnToggleSave) }) {
                        Icon(
                            imageVector = if (state.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Toggle Favorite"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Placeholder for the main content
        Text("Content Area", modifier = Modifier.padding(innerPadding))
    }
}

// --- Previews ---
@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme {
        RecipeDetailScreen(
            state = RecipeDetailUiState(
                recipe = com.tomazcuber.yourcookbok.domain.model.Recipe(
                    id = "1",
                    name = "Spicy Arrabiata Penne",
                    imageUrl = "",
                    instructions = "Some instructions.",
                    ingredients = emptyList(),
                    category = "Vegetarian",
                    area = "Italian"
                ),
                isLoading = false,
                isSaved = true
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}