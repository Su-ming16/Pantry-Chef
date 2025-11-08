package com.example.pantrychef.data.repository

import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun searchRecipesByIngredient(ingredient: String): Flow<Result<List<Recipe>>>
    fun getRecipeDetail(recipeId: String): Flow<Result<RecipeDetail>>
    suspend fun addFavorite(recipe: Recipe)
    suspend fun removeFavorite(recipeId: String)
    fun getAllFavorites(): Flow<List<Recipe>>
    fun isFavorite(recipeId: String): Flow<Boolean>
}

