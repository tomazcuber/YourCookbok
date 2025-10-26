package com.tomazcuber.yourcookbok.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeListResponse(
    val meals: List<MealDbRecipeDto>?
)