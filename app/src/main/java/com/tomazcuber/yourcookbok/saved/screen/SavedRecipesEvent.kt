package com.tomazcuber.yourcookbok.saved.screen

import com.tomazcuber.yourcookbok.domain.model.Recipe

sealed class SavedRecipesEvent {
    data class OnUnsaveClick(val recipe: Recipe) : SavedRecipesEvent()
    data class OnRecipeClick(val recipe: Recipe) : SavedRecipesEvent()
}