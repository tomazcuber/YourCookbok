package com.tomazcuber.yourcookbok.data.remote.mapper

import com.tomazcuber.yourcookbok.data.remote.dto.MealDbRecipeDto
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import com.tomazcuber.yourcookbok.domain.model.Recipe

fun MealDbRecipeDto.toDomain(): Recipe {
    val ingredients = mutableListOf<Ingredient>()

    fun addIngredient(name: String?, measure: String?) {
        if (!name.isNullOrBlank()) {
            ingredients.add(Ingredient(name = name, measure = measure.orEmpty()))
        }
    }

    addIngredient(ingredient1, measure1)
    addIngredient(ingredient2, measure2)
    addIngredient(ingredient3, measure3)
    addIngredient(ingredient4, measure4)
    addIngredient(ingredient5, measure5)
    addIngredient(ingredient6, measure6)
    addIngredient(ingredient7, measure7)
    addIngredient(ingredient8, measure8)
    addIngredient(ingredient9, measure9)
    addIngredient(ingredient10, measure10)
    addIngredient(ingredient11, measure11)
    addIngredient(ingredient12, measure12)
    addIngredient(ingredient13, measure13)
    addIngredient(ingredient14, measure14)
    addIngredient(ingredient15, measure15)
    addIngredient(ingredient16, measure16)
    addIngredient(ingredient17, measure17)
    addIngredient(ingredient18, measure18)
    addIngredient(ingredient19, measure19)
    addIngredient(ingredient20, measure20)

    return Recipe(
        id = this.id,
        name = this.name,
        instructions = this.instructions.orEmpty(),
        imageUrl = this.imageUrl.orEmpty(),
        category = this.category.orEmpty(),
        area = this.area.orEmpty(),
        ingredients = ingredients
    )
}
