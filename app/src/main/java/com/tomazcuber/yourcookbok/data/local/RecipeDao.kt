package com.tomazcuber.yourcookbok.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM saved_recipes")
    fun getAll(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipe(recipe: RecipeEntity)

    @Query("SELECT COUNT(*) FROM saved_recipes WHERE id = :recipeId")
    fun isRecipeSaved(recipeId: String): Flow<Int>

    @Delete
    suspend fun delete(recipe: RecipeEntity)
}