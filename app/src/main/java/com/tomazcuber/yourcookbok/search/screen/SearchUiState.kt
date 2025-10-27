package com.tomazcuber.yourcookbok.search.screen

data class SearchUiState(
    val searchQuery: String = "",
    val recipes: List<RecipeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)