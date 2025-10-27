package com.tomazcuber.yourcookbok.data.local

import androidx.test.filters.SmallTest
import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isEqualTo
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
class RecipeDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: RecipeDatabase
    @Inject
    lateinit var dao: RecipeDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveRecipe_and_getAll_returnsSavedRecipe() = runTest {
        // Given
        val recipe = RecipeEntity(
            id = "123",
            name = "Test Recipe",
            imageUrl = "url",
            instructions = "instructions",
            ingredients = listOf(Ingredient("i1", "m1")),
            category = "cat",
            area = "area"
        )

        // When
        dao.saveRecipe(recipe)
        val allRecipes = dao.getAll().first()

        // Then
        expectThat(allRecipes).contains(recipe)
    }

    @Test
    fun isRecipeSaved_returnsCorrectValue() = runTest {
        // Given
        val recipeId = "456"
        val recipe = RecipeEntity(
            id = recipeId,
            name = "Another Recipe",
            imageUrl = "url",
            instructions = "instructions",
            ingredients = emptyList(),
            category = "cat",
            area = "area"
        )

        // When / Then
        var isSaved = dao.isRecipeSaved(recipeId).first()
        expectThat(isSaved).isEqualTo(0) // Should not be saved yet

        dao.saveRecipe(recipe)
        isSaved = dao.isRecipeSaved(recipeId).first()
        expectThat(isSaved).isEqualTo(1) // Should be saved now
    }

    @Test
    fun delete_removesRecipeFromDatabase() = runTest {
        // Given
        val recipe = RecipeEntity(
            id = "789",
            name = "Deletable Recipe",
            imageUrl = "url",
            instructions = "instructions",
            ingredients = emptyList(),
            category = "cat",
            area = "area"
        )
        dao.saveRecipe(recipe)

        // When
        dao.delete(recipe)
        val allRecipes = dao.getAll().first()

        // Then
        expectThat(allRecipes).doesNotContain(recipe)
    }

    @Test
    fun searchSavedRecipes_withMatchingQuery_returnsMatchingRecipes() = runTest {
        // Given
        val recipe1 = RecipeEntity("1", "Chicken Soup", "", "", emptyList(), "", "")
        val recipe2 = RecipeEntity("2", "Beef Stew", "", "", emptyList(), "", "")
        dao.saveRecipe(recipe1)
        dao.saveRecipe(recipe2)

        // When
        val results = dao.searchSavedRecipes("Soup")

        // Then
        expectThat(results).contains(recipe1)
        expectThat(results).doesNotContain(recipe2)
    }

    @Test
    fun searchSavedRecipes_withNonMatchingQuery_returnsEmptyList() = runTest {
        // Given
        val recipe1 = RecipeEntity("1", "Chicken Soup", "", "", emptyList(), "", "")
        dao.saveRecipe(recipe1)

        // When
        val results = dao.searchSavedRecipes("Pizza")

        // Then
        expectThat(results).isEqualTo(emptyList())
    }

    @Test
    fun searchSavedRecipes_isCaseInsensitive() = runTest {
        // Given
        val recipe1 = RecipeEntity("1", "Chicken Soup", "", "", emptyList(), "", "")
        dao.saveRecipe(recipe1)

        // When
        val results = dao.searchSavedRecipes("chicken") // Lowercase query

        // Then
        expectThat(results).contains(recipe1)
    }

    @Test
    fun searchSavedRecipes_withQueryMatchingMultiple_returnsAllMatches() = runTest {
        // Given
        val recipe1 = RecipeEntity("1", "Chicken Soup", "", "", emptyList(), "", "")
        val recipe2 = RecipeEntity("2", "Spicy Chicken Wings", "", "", emptyList(), "", "")
        val recipe3 = RecipeEntity("3", "Beef Stew", "", "", emptyList(), "", "")
        dao.saveRecipe(recipe1)
        dao.saveRecipe(recipe2)
        dao.saveRecipe(recipe3)

        // When
        val results = dao.searchSavedRecipes("Chicken")

        // Then
        expectThat(results).contains(recipe1, recipe2)
        expectThat(results).doesNotContain(recipe3)
    }
}