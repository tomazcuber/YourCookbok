package com.tomazcuber.yourcookbok.data.di

import android.content.Context
import androidx.room.Room
import com.tomazcuber.yourcookbok.data.DatabaseConstants
import com.tomazcuber.yourcookbok.data.MealApiConstants
import com.tomazcuber.yourcookbok.data.local.RecipeDao
import com.tomazcuber.yourcookbok.data.local.RecipeDatabase
import com.tomazcuber.yourcookbok.data.remote.api.MealDbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Provides
    @Singleton
    fun provideRecipeDatabase(@ApplicationContext context: Context): RecipeDatabase {
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
    fun provideRecipeDao(recipeDatabase: RecipeDatabase): RecipeDao {
        return recipeDatabase.recipeDao()
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MealApiConstants.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideMealDbApiService(retrofit: Retrofit): MealDbApiService {
        return retrofit.create(MealDbApiService::class.java)
    }
}