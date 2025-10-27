package com.tomazcuber.yourcookbok.data.local.mapper

import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity
import com.tomazcuber.yourcookbok.domain.model.Recipe

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        instructions = this.instructions,
        ingredients = this.ingredients,
        category = this.category,
        area = this.area
    )
}

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        instructions = this.instructions,
        ingredients = this.ingredients,
        category = this.category,
        area = this.area
    )
}
