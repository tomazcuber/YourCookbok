package com.tomazcuber.yourcookbok.data.di

import android.content.Context
import androidx.room.Room
import com.tomazcuber.yourcookbok.data.DatabaseConstants
import com.tomazcuber.yourcookbok.data.local.RecipeDao
import com.tomazcuber.yourcookbok.data.local.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Provides
    @Singleton
    fun provideRecipeDatabase(@ApplicationContext context: Context) : RecipeDatabase {
        return Room.databaseBuilder(
            context = context,
            RecipeDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(recipeDatabase: RecipeDatabase) : RecipeDao {
        return recipeDatabase.recipeDao()
    }
}