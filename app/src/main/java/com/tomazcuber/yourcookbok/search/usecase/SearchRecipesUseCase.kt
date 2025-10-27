package com.tomazcuber.yourcookbok.search.usecase

import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.repository.RecipeRepository
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(query: String): Result<List<Recipe>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        return recipeRepository.searchRecipes(query)
    }
}