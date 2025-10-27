package com.tomazcuber.yourcookbok.data.repository

import com.tomazcuber.yourcookbok.data.local.RecipeDao
import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity
import com.tomazcuber.yourcookbok.data.remote.api.MealDbApiService
import com.tomazcuber.yourcookbok.data.remote.dto.MealDbRecipeDto
import com.tomazcuber.yourcookbok.data.remote.dto.RecipeListResponse
import com.tomazcuber.yourcookbok.domain.model.Recipe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isSuccess
import java.io.IOException

@ExtendWith(MockKExtension::class)
class RecipeRepositoryImplTest {

    @RelaxedMockK
    private lateinit var mealDbApiService: MealDbApiService

    @RelaxedMockK
    private lateinit var recipeDao: RecipeDao

    private lateinit var repository: RecipeRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = RecipeRepositoryImpl(mealDbApiService, recipeDao)
    }

    @Nested
    @DisplayName("searchRecipes()")
    inner class SearchRecipes {

        @Test
        fun `when network is successful, then return success with network data`() = runTest {
            // Given
            val networkDto = MealDbRecipeDto(id = "net123", name = "Network Recipe")
            val expectedRecipe = Recipe(id = "net123", name = "Network Recipe", instructions = "", imageUrl = "", category = "", area = "", ingredients = emptyList())
            coEvery { mealDbApiService.searchRecipes(any()) } returns RecipeListResponse(listOf(networkDto))

            // When
            val result = repository.searchRecipes("test")

            // Then
            expectThat(result).isSuccess().isEqualTo(listOf(expectedRecipe))
            coVerify(exactly = 1) { mealDbApiService.searchRecipes("test") }
            coVerify(exactly = 0) { recipeDao.searchSavedRecipes(any()) } // DAO should not be called
        }

        @Test
        fun `when network fails but local data exists, then return success with local data`() = runTest {
            // Given
            val localEntity = RecipeEntity("local456", "Local Recipe", "", "", emptyList(), "", "")
            val expectedRecipe = Recipe(id = "local456", name = "Local Recipe", instructions = "", imageUrl = "", category = "", area = "", ingredients = emptyList())
            coEvery { mealDbApiService.searchRecipes(any()) } throws IOException("Network failed")
            coEvery { recipeDao.searchSavedRecipes(any()) } returns listOf(localEntity)

            // When
            val result = repository.searchRecipes("test")

            // Then
            expectThat(result).isSuccess().isEqualTo(listOf(expectedRecipe))
            coVerify(exactly = 1) { mealDbApiService.searchRecipes("test") }
            coVerify(exactly = 1) { recipeDao.searchSavedRecipes("test") } // DAO should be called as a fallback
        }

        @Test
        fun `when network fails and no local data exists, then return failure`() = runTest {
            // Given
            val networkException = IOException("Network failed")
            coEvery { mealDbApiService.searchRecipes(any()) } throws networkException
            coEvery { recipeDao.searchSavedRecipes(any()) } returns emptyList()

            // When
            val result = repository.searchRecipes("test")

            // Then
            expectThat(result.isFailure).isEqualTo(true)
            expectThat(result.exceptionOrNull()).isA<IOException>()
            coVerify(exactly = 1) { mealDbApiService.searchRecipes("test") }
            coVerify(exactly = 1) { recipeDao.searchSavedRecipes("test") }
        }
    }

    @Nested
    @DisplayName("Database Operations")
    inner class DatabaseOperations {

        @Test
        fun `saveRecipe should call dao with mapped entity`() = runTest {
            // Given
            val recipe = Recipe(id = "1", name = "Test Recipe", instructions = "", imageUrl = "", category = "", area = "", ingredients = emptyList())
            val expectedEntity = RecipeEntity("1", "Test Recipe", "", "", emptyList(), "", "")

            // When
            repository.saveRecipe(recipe)

            // Then
            coVerify { recipeDao.saveRecipe(expectedEntity) }
        }

        @Test
        fun `deleteRecipe should call dao with mapped entity`() = runTest {
            // Given
            val recipe = Recipe(id = "1", name = "Test Recipe", instructions = "", imageUrl = "", category = "", area = "", ingredients = emptyList())
            val expectedEntity = RecipeEntity("1", "Test Recipe", "", "", emptyList(), "", "")

            // When
            repository.deleteRecipe(recipe)

            // Then
            coVerify { recipeDao.delete(expectedEntity) }
        }

        @Test
        fun `getSavedRecipes should map entities from dao to domain`() = runTest {
            // Given
            val entity = RecipeEntity("1", "Local Recipe", "", "", emptyList(), "", "")
            val expectedDomain = Recipe(id = "1", name = "Local Recipe", instructions = "", imageUrl = "", category = "", area = "", ingredients = emptyList())
            every { recipeDao.getAll() } returns kotlinx.coroutines.flow.flowOf(listOf(entity))

            // When
            val resultFlow = repository.getSavedRecipes()

            // Then
            val resultList = resultFlow.first()
            expectThat(resultList).isEqualTo(listOf(expectedDomain))
            io.mockk.verify { recipeDao.getAll() }
        }

        @Test
        fun `isRecipeSaved should map count from dao to boolean`() = runTest {
            // Given
            every { recipeDao.isRecipeSaved("1") } returns kotlinx.coroutines.flow.flowOf(1)
            every { recipeDao.isRecipeSaved("2") } returns kotlinx.coroutines.flow.flowOf(0)

            // When
            val isSaved = repository.isRecipeSaved("1").first()
            val isNotSaved = repository.isRecipeSaved("2").first()

            // Then
            expectThat(isSaved).isEqualTo(true)
            expectThat(isNotSaved).isEqualTo(false)
        }
    }
}