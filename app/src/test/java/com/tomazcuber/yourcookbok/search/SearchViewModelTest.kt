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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setUp() {
        // Given
        every { getSavedRecipesUseCase() } returns flowOf(emptyList())
        coEvery { searchRecipesUseCase(any()) } returns Result.success(emptyList())

        viewModel = SearchViewModel(
            searchRecipesUseCase,
            getSavedRecipesUseCase,
            saveRecipeUseCase,
            deleteRecipeUseCase
        )
    }

    @Test
    fun `when search query changes, state is updated and search is triggered after debounce`() = runTest {
        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("chicken"))

        // Then
        expectThat(viewModel.uiState.value.searchQuery).isEqualTo("chicken")
        coVerify(exactly = 0) { searchRecipesUseCase(any()) } // Not called yet

        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(500) // Advance time to trigger debounce
        runCurrent() // Execute the search coroutine

        coVerify(exactly = 1) { searchRecipesUseCase("chicken") }
    }

    @Test
    fun `when search submitted, search is triggered immediately`() = runTest {
        // Given
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("beef"))

        // When
        viewModel.onEvent(SearchEvent.OnSearchSubmit)
        runCurrent()

        // Then
        coVerify(exactly = 1) { searchRecipesUseCase("beef") }
    }

    @Test
    fun `when search succeeds, state is updated with recipes`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")
        coEvery { searchRecipesUseCase("chicken") } returns Result.success(listOf(recipe))

        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("chicken"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)

        // Then
        val state = viewModel.uiState.value
        expectThat(state.isLoading).isFalse()
        expectThat(state.recipes.first().recipe).isEqualTo(recipe)
    }

    @Test
    fun `when search fails, state is updated with error message`() = runTest {
        // Given
        coEvery { searchRecipesUseCase("fail") } returns Result.failure(IOException("Network Error"))

        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("fail"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)

        // Then
        val state = viewModel.uiState.value
        expectThat(state.isLoading).isFalse()
        expectThat(state.userMessage).isEqualTo("Network Error")
    }

    @Test
    fun `when recipe is saved, isSaved flag is true in UI model`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")
        every { getSavedRecipesUseCase() } returns flowOf(listOf(recipe)) // This recipe is saved
        coEvery { searchRecipesUseCase("chicken") } returns Result.success(listOf(recipe))

        // Re-create viewModel to pick up the new flow
        viewModel = SearchViewModel(searchRecipesUseCase, getSavedRecipesUseCase, saveRecipeUseCase, deleteRecipeUseCase)

        // When
        viewModel.onEvent(SearchEvent.OnSearchQueryChanged("chicken"))
        mainCoroutineRule.testDispatcher.scheduler.advanceTimeBy(501)

        // Then
        val uiModel = viewModel.uiState.value.recipes.first()
        expectThat(uiModel.isSaved).isTrue()
    }

    @Test
    fun `onToggleSave for unsaved recipe should call saveRecipeUseCase`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")
        // Saved list is empty by default

        // When
        viewModel.onEvent(SearchEvent.OnToggleSave(recipe))
        runCurrent() // Execute the launched coroutine

        // Then
        coVerify(exactly = 1) { saveRecipeUseCase(recipe) }
        coVerify(exactly = 0) { deleteRecipeUseCase(any()) }
    }

    @Test
    fun `onToggleSave for saved recipe should call deleteRecipeUseCase`() = runTest {
        // Given
        val recipe = Recipe("1", "Chicken", "", "", emptyList(), "", "")
        every { getSavedRecipesUseCase() } returns flowOf(listOf(recipe)) // Pretend recipe is saved
        // Re-create viewModel to pick up the new flow
        viewModel = SearchViewModel(searchRecipesUseCase, getSavedRecipesUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent() // Ensure the initial savedRecipeIds flow is collected

        // When
        viewModel.onEvent(SearchEvent.OnToggleSave(recipe))
        runCurrent() // Execute the toggle coroutine

        // Then
        coVerify(exactly = 1) { deleteRecipeUseCase(recipe) }
        coVerify(exactly = 0) { saveRecipeUseCase(any()) }
    }
}