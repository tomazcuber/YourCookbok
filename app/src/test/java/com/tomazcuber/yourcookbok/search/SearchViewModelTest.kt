package com.tomazcuber.yourcookbok.search

import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.GetSavedRecipesUseCase
import com.tomazcuber.yourcookbok.domain.usecase.SaveRecipeUseCase
import com.tomazcuber.yourcookbok.search.screen.SearchEvent
import com.tomazcuber.yourcookbok.search.usecase.SearchRecipesUseCase
import com.tomazcuber.yourcookbok.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.io.IOException

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class SearchViewModelTest {

    @JvmField
    @RegisterExtension
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    private lateinit var searchRecipesUseCase: SearchRecipesUseCase

    @RelaxedMockK
    private lateinit var getSavedRecipesUseCase: GetSavedRecipesUseCase

    @RelaxedMockK
    private lateinit var saveRecipeUseCase: SaveRecipeUseCase

    @RelaxedMockK
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase

    private lateinit var savedRecipesFlow: MutableStateFlow<List<Recipe>>

    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setUp() {
        savedRecipesFlow = MutableStateFlow(emptyList())
        every { getSavedRecipesUseCase() } returns savedRecipesFlow
        coEvery { searchRecipesUseCase(any()) } returns Result.success(emptyList())

        viewModel = SearchViewModel(
            searchRecipesUseCase,
            getSavedRecipesUseCase,
            saveRecipeUseCase,
            deleteRecipeUseCase
        )
    }

    @Test
    fun `when search query changes, search is triggered after debounce`() = runTest {
        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("chicken"))
        expectThat(viewModel.uiState.value.searchQuery).isEqualTo("chicken")

        // Then
        coVerify(exactly = 0) { searchRecipesUseCase(any()) } // Not called yet
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)
        runCurrent()
        coVerify(exactly = 1) { searchRecipesUseCase("chicken") }
    }

    @Test
    fun `when search fails, state is updated with error message`() = runTest {
        // Given
        coEvery { searchRecipesUseCase("fail") } returns Result.failure(IOException("Network Error"))

        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("fail"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)
        runCurrent()

        // Then
        val state = viewModel.uiState.value
        expectThat(state.isLoading).isFalse()
        expectThat(state.userMessage).isEqualTo("Network Error")
    }

    @Test
    fun `when search succeeds, the ui state is updated with correct saved status`() = runTest {
        // Given
        val recipe1 = Recipe("1", "Saved Recipe", "", "", emptyList(), "", "")
        val recipe2 = Recipe("2", "Unsaved Recipe", "", "", emptyList(), "", "")
        savedRecipesFlow.value = listOf(recipe1) // recipe1 is saved
        coEvery { searchRecipesUseCase("Recipe") } returns Result.success(listOf(recipe1, recipe2))

        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("Recipe"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)
        runCurrent()

        // Then
        val recipes = viewModel.uiState.value.recipes
        expectThat(recipes.find { it.recipe.id == "1" }?.isSaved).isTrue()
        expectThat(recipes.find { it.recipe.id == "2" }?.isSaved).isFalse()
    }

    @Test
    fun `when a recipe is saved via flow update, the ui state is updated to reflect it`() = runTest {
        // Given
        val recipe = Recipe("1", "Test Recipe", "", "", emptyList(), "", "")
        coEvery { searchRecipesUseCase("Test") } returns Result.success(listOf(recipe))
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("Test"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)
        runCurrent()

        // Assert initial state is unsaved
        expectThat(viewModel.uiState.value.recipes.first().isSaved).isFalse()

        // When: the saved recipes flow emits a new list containing the recipe
        savedRecipesFlow.value = listOf(recipe)
        runCurrent() // Allow the combine operator to run

        // Then: the UI model in the state should now be marked as saved
        expectThat(viewModel.uiState.value.recipes.first().isSaved).isTrue()
    }

    @Test
    fun `onToggleSave for unsaved recipe should call saveRecipeUseCase`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")

        // When
        viewModel.onEvent(SearchEvent.OnToggleSave(recipe))
        runCurrent()

        // Then
        coVerify(exactly = 1) { saveRecipeUseCase(recipe) }
        coVerify(exactly = 0) { deleteRecipeUseCase(any()) }
    }

    @Test
    fun `onToggleSave for saved recipe should call deleteRecipeUseCase`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")
        savedRecipesFlow.value = listOf(recipe) // Pretend recipe is saved
        runCurrent()

        // When
        viewModel.onEvent(SearchEvent.OnToggleSave(recipe))
        runCurrent()

        // Then
        coVerify(exactly = 1) { deleteRecipeUseCase(recipe) }
        coVerify(exactly = 0) { saveRecipeUseCase(any()) }
    }
}