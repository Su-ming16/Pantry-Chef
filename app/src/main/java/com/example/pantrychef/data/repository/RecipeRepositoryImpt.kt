package com.example.pantrychef.data.repository

import com.example.pantrychef.data.database.FavoriteRecipeDao
import com.example.pantrychef.data.database.IngredientDao
import com.example.pantrychef.data.mapper.toRecipe
import com.example.pantrychef.data.mapper.toRecipeDetail
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.network.TheMealDbApiService
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(
    private val ingredientDao: IngredientDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val apiService: TheMealDbApiService
) : RecipeRepository {

    // --- Ingredient Implementations ---
    override fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()
    override suspend fun addIngredient(ingredient: Ingredient) = ingredientDao.insertIngredient(ingredient)
    override suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.deleteIngredient(ingredient)
    override fun searchIngredients(query: String): Flow<List<Ingredient>> = ingredientDao.searchIngredients(query)

    // --- Recipe Implementations ---
    override suspend fun searchRecipesByIngredient(ingredientName: String): Result<List<Recipe>> {
        return try {
            val response = apiService.searchRecipesByIngredient(ingredientName)
            val recipes = response.meals?.map { it.toRecipe() } ?: emptyList()
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(recipeId: String): Result<RecipeDetail?> {
        return try {
            val response = apiService.getRecipeDetails(recipeId)
            val recipeDetail = response.meals?.firstOrNull()?.toRecipeDetail()
            Result.success(recipeDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Favorite Implementations ---
    override fun getAllFavorites(): Flow<List<FavoriteRecipe>> = favoriteRecipeDao.getAllFavorites()
    override fun isFavorite(recipeId: String): Flow<Boolean> = favoriteRecipeDao.isFavorite(recipeId)
    override suspend fun removeFavorite(recipeId: String) = favoriteRecipeDao.deleteFavoriteById(recipeId)

    override suspend fun addFavorite(recipe: Recipe) {
        val favorite = FavoriteRecipe(
            id = recipe.id,
            name = recipe.name,
            imageUrl = recipe.thumbnail
        )
        favoriteRecipeDao.insertFavorite(favorite)
    }
}