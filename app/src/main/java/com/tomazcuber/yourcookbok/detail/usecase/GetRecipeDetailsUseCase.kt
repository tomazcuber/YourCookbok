package com.tomazcuber.yourcookbok.detail.usecase

import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeDetailsUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Result<Recipe> {
        return repository.getRecipeDetails(id)
    }
}