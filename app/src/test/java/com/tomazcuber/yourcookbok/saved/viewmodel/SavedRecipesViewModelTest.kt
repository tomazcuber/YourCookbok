package com.tomazcuber.yourcookbok.saved.viewmodel

import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.usecase.DeleteRecipeUseCase
import com.tomazcuber.yourcookbok.domain.usecase.GetSavedRecipesUseCase
import com.tomazcuber.yourcookbok.saved.screen.SavedRecipesEvent
import com.tomazcuber.yourcookbok.util.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class SavedRecipesViewModelTest {

    @JvmField
    @RegisterExtension
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    private lateinit var getSavedRecipesUseCase: GetSavedRecipesUseCase

    @RelaxedMockK
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase

    @Test
    fun `state should expose recipes from use case`() = runTest {
        // Given
        val recipes = listOf(Recipe("1", "Recipe 1", "", "", emptyList(), "", ""))
        every { getSavedRecipesUseCase() } returns flowOf(recipes)

        // When
        val viewModel = SavedRecipesViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)

        // Then: The initial value should be empty
        expectThat(viewModel.savedRecipes.value).isEqualTo(emptyList())

        // Start collecting in the background to trigger the stateIn operator
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.savedRecipes.collect()
        }

        // Run the collection coroutine that was queued by stateIn
        runCurrent()

        // The value should now be updated to the emitted list
        expectThat(viewModel.savedRecipes.value).isEqualTo(recipes)

        job.cancel() // Clean up the collector
    }

    @Test
    fun `onEvent OnUnsaveClick should call deleteRecipeUseCase`() = runTest {
        // Given
        val recipe = Recipe("1", "Recipe 1", "", "", emptyList(), "", "")
        val viewModel = SavedRecipesViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)


        // When
        viewModel.onEvent(SavedRecipesEvent.OnUnsaveClick(recipe))
        runCurrent()

        // Then
        coVerify { deleteRecipeUseCase(recipe) }
    }
}