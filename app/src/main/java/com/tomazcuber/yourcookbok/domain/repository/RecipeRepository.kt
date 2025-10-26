package com.tomazcuber.yourcookbok.domain.repository

import com.tomazcuber.yourcookbok.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipes(query: String): Result<List<Recipe>>

    suspend fun saveRecipe(recipe: Recipe): Result<Unit>

    suspend fun deleteRecipe(recipe: Recipe): Result<Unit>

    fun getSavedRecipes(): Flow<List<Recipe>>

    fun isRecipeSaved(id: String): Flow<Boolean>
}