package com.tomazcuber.yourcookbok.search

import com.tomazcuber.yourcookbok.domain.model.Recipe

data class RecipeUiModel(
    val recipe: Recipe,
    val isSaved: Boolean
)