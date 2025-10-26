package com.tomazcuber.yourcookbok.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val name: String,
    val measure: String,
)
