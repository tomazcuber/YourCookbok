package com.tomazcuber.yourcookbok.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.tomazcuber.yourcookbok.detail.usecase.GetRecipeDetailsUseCase
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.model.RecipeError
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.IsRecipeSavedUseCase
import com.tomazcuber.yourcookbok.domain.usecase.SaveRecipeUseCase
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

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class RecipeDetailViewModelTest {

    @JvmField
    @RegisterExtension
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    private lateinit var getRecipeDetailsUseCase: GetRecipeDetailsUseCase

    @RelaxedMockK
    private lateinit var isRecipeSavedUseCase: IsRecipeSavedUseCase

    @RelaxedMockK
    private lateinit var saveRecipeUseCase: SaveRecipeUseCase

    @RelaxedMockK
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: RecipeDetailViewModel

    private val recipeId = "123"

    @BeforeEach
    fun setUp() {
        savedStateHandle = SavedStateHandle(mapOf("recipeId" to recipeId))
        every { isRecipeSavedUseCase(recipeId) } returns MutableStateFlow(false)
    }

    @Test
    fun `init should fetch recipe details and update state on success`() = runTest {
        // Given
        val recipe = Recipe(recipeId, "Test Recipe", "", "", emptyList(), "", "")
        coEvery { getRecipeDetailsUseCase(recipeId) } returns Result.success(recipe)

        // When
        viewModel = RecipeDetailViewModel(savedStateHandle, getRecipeDetailsUseCase, isRecipeSavedUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent()

        // Then
        val state = viewModel.uiState.value
        expectThat(state.isLoading).isFalse()
        expectThat(state.recipe).isEqualTo(recipe)
    }

    @Test
    fun `init should update state with error message on failure`() = runTest {
        // Given
        coEvery { getRecipeDetailsUseCase(recipeId) } returns Result.failure(RecipeError.RecipeNotFound)

        // When
        viewModel = RecipeDetailViewModel(savedStateHandle, getRecipeDetailsUseCase, isRecipeSavedUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent()

        // Then
        val state = viewModel.uiState.value
        expectThat(state.isLoading).isFalse()
        expectThat(state.userMessage).isEqualTo("Recipe not found.")
    }

    @Test
    fun `isSaved status should be observed and updated in state`() = runTest {
        // Given
        val isSavedFlow = MutableStateFlow(false)
        every { isRecipeSavedUseCase(recipeId) } returns isSavedFlow
        coEvery { getRecipeDetailsUseCase(recipeId) } returns Result.success(Recipe(recipeId, "", "", "", emptyList(), "", ""))

        // When
        viewModel = RecipeDetailViewModel(savedStateHandle, getRecipeDetailsUseCase, isRecipeSavedUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent()

        // Then
        expectThat(viewModel.uiState.value.isSaved).isFalse()

        // When
        isSavedFlow.value = true
        runCurrent()

        // Then
        expectThat(viewModel.uiState.value.isSaved).isTrue()
    }

    @Test
    fun `onToggleSave should call saveRecipeUseCase when not saved`() = runTest {
        // Given
        val recipe = Recipe(recipeId, "Test Recipe", "", "", emptyList(), "", "")
        coEvery { getRecipeDetailsUseCase(recipeId) } returns Result.success(recipe)
        every { isRecipeSavedUseCase(recipeId) } returns MutableStateFlow(false)
        viewModel = RecipeDetailViewModel(savedStateHandle, getRecipeDetailsUseCase, isRecipeSavedUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent()

        // When
        viewModel.onEvent(com.tomazcuber.yourcookbok.detail.screen.RecipeDetailEvent.OnToggleSave)
        runCurrent()

        // Then
        coVerify { saveRecipeUseCase(recipe) }
    }

    @Test
    fun `onToggleSave should call deleteRecipeUseCase when saved`() = runTest {
        // Given
        val recipe = Recipe(recipeId, "Test Recipe", "", "", emptyList(), "", "")
        coEvery { getRecipeDetailsUseCase(recipeId) } returns Result.success(recipe)
        every { isRecipeSavedUseCase(recipeId) } returns MutableStateFlow(true)
        viewModel = RecipeDetailViewModel(savedStateHandle, getRecipeDetailsUseCase, isRecipeSavedUseCase, saveRecipeUseCase, deleteRecipeUseCase)
        runCurrent()

        // When
        viewModel.onEvent(com.tomazcuber.yourcookbok.detail.screen.RecipeDetailEvent.OnToggleSave)
        runCurrent()

        // Then
        coVerify { deleteRecipeUseCase(recipe) }
    }
}