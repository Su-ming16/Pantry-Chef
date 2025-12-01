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
        availableIngredients: List<String>,
        availableEquipment: List<String> = emptyList()
    ): Result<Pair<List<Recipe>, List<Recipe>>>
    suspend fun getRecipeDetails(recipeId: String, availableIngredients: List<String> = emptyList()): Result<RecipeDetail?>

    // --- Favorite Functions ---
    fun getAllFavorites(): Flow<List<FavoriteRecipe>>
    fun isFavorite(recipeId: String): Flow<Boolean>
    suspend fun addFavorite(recipe: Recipe) // Accepts the clean Recipe model
    suspend fun removeFavorite(recipeId: String)

    // --- Equipment Functions ---
    fun getAllEquipment(): Flow<List<com.example.pantrychef.data.model.Equipment>>
    suspend fun addEquipment(equipment: com.example.pantrychef.data.model.Equipment)
    suspend fun deleteEquipment(equipment: com.example.pantrychef.data.model.Equipment)
}