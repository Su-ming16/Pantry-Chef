package com.example.pantrychef.data.repository

import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    // --- Ingredient Functions ---
    fun getAllIngredients(): Flow<List<Ingredient>>
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun deleteIngredient(ingredient: Ingredient)
    fun searchIngredients(query: String): Flow<List<Ingredient>>

    // --- Recipe Functions (Using Clean Domain Models) ---
    suspend fun searchRecipesByIngredient(ingredientName: String): Result<List<Recipe>>
    suspend fun searchRecipesWithSmartMatching(
        availableIngredients: List<String>
    ): Result<Pair<List<Recipe>, List<Recipe>>>
    suspend fun getRecipeDetails(recipeId: String, availableIngredients: List<String> = emptyList()): Result<RecipeDetail?>

    // --- Favorite Functions ---
    fun getAllFavorites(): Flow<List<FavoriteRecipe>>
    fun isFavorite(recipeId: String): Flow<Boolean>
    suspend fun addFavorite(recipe: Recipe)
    suspend fun removeFavorite(recipeId: String)
    
    // --- User Preference Functions ---
    fun getUserPreference(): Flow<com.example.pantrychef.data.model.UserPreference?>
    suspend fun updateUserPreference(preference: com.example.pantrychef.data.model.UserPreference)
}
