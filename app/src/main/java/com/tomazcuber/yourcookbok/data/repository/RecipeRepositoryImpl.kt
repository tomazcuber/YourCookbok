package com.tomazcuber.yourcookbok.data.repository

import com.tomazcuber.yourcookbok.data.local.RecipeDao
import com.tomazcuber.yourcookbok.data.local.mapper.toDomain
import com.tomazcuber.yourcookbok.data.local.mapper.toEntity
import com.tomazcuber.yourcookbok.data.remote.api.MealDbApiService
import com.tomazcuber.yourcookbok.data.remote.mapper.toDomain
import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.model.RecipeError
import com.tomazcuber.yourcookbok.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val mealDbApiService: MealDbApiService,
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override suspend fun getRecipeDetails(id: String): Result<Recipe> {
        return try {
            // First, try to get from the local database by ID
            val localRecipe = recipeDao.findById(id)
            if (localRecipe != null) {
                return Result.success(localRecipe.toDomain())
            }

            // If not in local, fetch from network
            val response = mealDbApiService.lookupRecipeById(id)
            val recipe = response.meals?.firstOrNull()?.toDomain()
            if (recipe != null) {
                Result.success(recipe)
            } else {
                Result.failure(RecipeError.RecipeNotFound)
            }
        } catch (e: Exception) {
            Result.failure(RecipeError.NetworkError(e))
        }
    }

    override suspend fun searchRecipes(query: String): Result<List<Recipe>> {
        return try {
            val response = mealDbApiService.searchRecipes(query)
            val recipes = response.meals?.map { it.toDomain() } ?: emptyList()
            Result.success(recipes)
        } catch (e: Exception) {
            try {
                val localRecipes = recipeDao.searchSavedRecipes(query).map { it.toDomain() }
                if (localRecipes.isNotEmpty()) {
                    Result.success(localRecipes)
                } else {
                    Result.failure(RecipeError.NetworkError(e))
                }
            } catch (dbException: Exception) {
                Result.failure(RecipeError.DatabaseError(dbException))
            }
        }
    }

    override suspend fun saveRecipe(recipe: Recipe): Result<Unit> {
        return try {
            recipeDao.saveRecipe(recipe.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RecipeError.DatabaseError(e))
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Result<Unit> {
        return try {
            recipeDao.delete(recipe.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RecipeError.DatabaseError(e))
        }
    }

    override fun getSavedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isRecipeSaved(id: String): Flow<Boolean> {
        return recipeDao.isRecipeSaved(id).map { count ->
            count > 0
        }
    }
}
