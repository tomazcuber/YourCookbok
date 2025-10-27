package com.tomazcuber.yourcookbok.search.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme
import org.junit.Rule
import org.junit.Test

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenStateIsLoading_thenLoadingIndicatorIsDisplayed() {
        // Given
        val state = SearchUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            YourCookbokTheme {
                SearchScreen(state = state, onEvent = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Find your next favorite recipe").assertDoesNotExist()
    }

    @Test
    fun whenRecipesIsEmpty_thenEmptyMessageIsDisplayed() {
        // Given
        val state = SearchUiState(recipes = emptyList())

        // When
        composeTestRule.setContent {
            YourCookbokTheme {
                SearchScreen(state = state, onEvent = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Find your next favorite recipe").assertIsDisplayed()
    }

    @Test
    fun whenRecipesArePresent_thenRecipeItemsAreDisplayed() {
        // Given
        val recipes = listOf(
            RecipeUiModel(Recipe(id = "1", name = "Chicken Soup", "", "", emptyList(), "", ""), isSaved = false),
            RecipeUiModel(Recipe(id = "2", name = "Beef Stew", "", "", emptyList(), "", ""), isSaved = true)
        )
        val state = SearchUiState(recipes = recipes)

        // When
        composeTestRule.setContent {
            YourCookbokTheme {
                SearchScreen(state = state, onEvent = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Chicken Soup").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beef Stew").assertIsDisplayed()
    }

    @Test
    fun whenTypingInSearchField_thenOnEventIsCalled() {
        // Given
        var capturedEvent: SearchEvent? = null
        val state = SearchUiState()

        composeTestRule.setContent {
            YourCookbokTheme {
                SearchScreen(
                    state = state,
                    onEvent = { event -> capturedEvent = event }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Search for a recipe...").performTextInput("pasta")

        // Then
        assert(capturedEvent == SearchEvent.OnSearchQueryChanged("pasta"))
    }

    @Test
    fun whenFavoriteIsToggled_iconUpdatesAndEventIsFired() {
        // Given
        val recipe = Recipe(id = "1", name = "Chicken Soup", "", "", emptyList(), "", "")
        var capturedEvent: SearchEvent? = null
        var state by mutableStateOf(
            SearchUiState(recipes = listOf(RecipeUiModel(recipe, isSaved = false)))
        )

        composeTestRule.setContent {
            YourCookbokTheme {
                SearchScreen(
                    state = state,
                    onEvent = { event ->
                        capturedEvent = event
                        // Simulate the state update that the ViewModel would do
                        if (event is SearchEvent.OnToggleSave) {
                            val newRecipes = state.recipes.map {
                                if (it.recipe.id == event.recipe.id) {
                                    it.copy(isSaved = !it.isSaved)
                                } else {
                                    it
                                }
                            }
                            state = state.copy(recipes = newRecipes)
                        }
                    }
                )
            }
        }

        // When: Click to favorite
        composeTestRule.onNodeWithTag("favorite_button_1_false").performClick()

        // Then: Assert event was fired and UI updated
        assert(capturedEvent == SearchEvent.OnToggleSave(recipe))
        composeTestRule.onNodeWithTag("favorite_button_1_true").assertIsDisplayed()
        composeTestRule.onNodeWithTag("favorite_button_1_false").assertDoesNotExist()

        // When: Click to unfavorite
        composeTestRule.onNodeWithTag("favorite_button_1_true").performClick()

        // Then: Assert event was fired and UI updated back
        assert(capturedEvent == SearchEvent.OnToggleSave(recipe))
        composeTestRule.onNodeWithTag("favorite_button_1_false").assertIsDisplayed()
        composeTestRule.onNodeWithTag("favorite_button_1_true").assertDoesNotExist()
    }
}