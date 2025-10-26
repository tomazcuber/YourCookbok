package com.tomazcuber.yourcookbok.data.local.converter

import androidx.room.TypeConverter
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IngredientListConverter {
    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        return Json.encodeToString(ingredients)
    }

    @TypeConverter
    fun toIngredientList(json: String): List<Ingredient> {
        return Json.decodeFromString(json)
    }
}
