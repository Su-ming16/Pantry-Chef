package com.example.pantrychef.data.network

import com.example.pantrychef.data.network.dto.MealsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDbApiService {

    @GET("api/json/v1/1/filter.php")
    suspend fun searchRecipesByIngredient(
        @Query("i") ingredient: String
    ): MealsResponseDto

    @GET("api/json/v1/1/filter.php")
    suspend fun searchRecipesByCategory(
        @Query("c") category: String
    ): MealsResponseDto

    @GET("api/json/v1/1/lookup.php")
    suspend fun getRecipeDetails(
        @Query("i") id: String
    ): MealsResponseDto
}