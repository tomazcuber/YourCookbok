package com.tomazcuber.yourcookbok.domain.usecase

import com.tomazcuber.yourcookbok.domain.model.Recipe
import com.tomazcuber.yourcookbok.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe): Result<Unit> {
        return repository.deleteRecipe(recipe)
    }
}