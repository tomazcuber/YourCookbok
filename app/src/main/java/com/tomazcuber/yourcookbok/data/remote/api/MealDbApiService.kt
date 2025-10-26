package com.tomazcuber.yourcookbok.data.remote.api

import com.tomazcuber.yourcookbok.data.remote.dto.RecipeListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApiService {

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): RecipeListResponse

    @GET("lookup.php")
    suspend fun lookupRecipeById(
        @Query("i") id: String
    ): RecipeListResponse
}
