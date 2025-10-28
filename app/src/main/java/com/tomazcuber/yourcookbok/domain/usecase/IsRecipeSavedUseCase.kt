package com.tomazcuber.yourcookbok.domain.usecase

import com.tomazcuber.yourcookbok.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsRecipeSavedUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: String): Flow<Boolean> {
        return repository.isRecipeSaved(id)
    }
}