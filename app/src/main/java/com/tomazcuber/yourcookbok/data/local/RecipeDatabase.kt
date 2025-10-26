package com.tomazcuber.yourcookbok.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomazcuber.yourcookbok.data.local.converter.IngredientListConverter
import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1)
@TypeConverters(IngredientListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}