package com.tomazcuber.yourcookbok.detail.screen

import com.tomazcuber.yourcookbok.domain.model.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val numberedInstructions: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val isLoading: Boolean = true,
    val userMessage: String? = null
)