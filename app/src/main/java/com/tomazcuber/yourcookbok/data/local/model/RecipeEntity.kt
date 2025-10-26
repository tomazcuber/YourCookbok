package com.tomazcuber.yourcookbok.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.tomazcuber.yourcookbok.domain.model.Ingredient

import com.tomazcuber.yourcookbok.data.DatabaseConstants.RECIPE_TABLE_NAME

@Entity(tableName = RECIPE_TABLE_NAME)
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val category: String,
    val area: String,
)
