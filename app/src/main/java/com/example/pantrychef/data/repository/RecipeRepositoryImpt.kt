package com.example.pantrychef.data.repository

import com.example.pantrychef.data.database.EquipmentDao
import com.example.pantrychef.data.database.FavoriteRecipeDao
import com.example.pantrychef.data.database.IngredientDao
import com.example.pantrychef.data.mapper.toRecipe
import com.example.pantrychef.data.mapper.toRecipeDetail
import com.example.pantrychef.data.model.Equipment
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.model.RecipeMatchType
import com.example.pantrychef.data.network.TheMealDbApiService
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(
    private val ingredientDao: IngredientDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val equipmentDao: EquipmentDao,
    private val apiService: TheMealDbApiService
) : RecipeRepository {

    private val recipeDetailsCache = mutableMapOf<String, RecipeDetail>()

    override fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()
    override suspend fun addIngredient(ingredient: Ingredient) = ingredientDao.insertIngredient(ingredient)
    override suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.deleteIngredient(ingredient)
    override fun searchIngredients(query: String): Flow<List<Ingredient>> = ingredientDao.searchIngredients(query)
    override suspend fun searchRecipesByIngredient(ingredientName: String): Result<List<Recipe>> {
        return try {
            val response = apiService.searchRecipesByIngredient(ingredientName)
            val recipes = response.meals?.map { it.toRecipe() } ?: emptyList()
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(recipeId: String, availableIngredients: List<String>): Result<RecipeDetail?> {
        return try {
            val cacheKey = "$recipeId-${availableIngredients.sorted().joinToString(",")}"
            val cached = recipeDetailsCache[cacheKey]
            if (cached != null) {
                return Result.success(cached)
            }
            
            val response = apiService.getRecipeDetails(recipeId)
            val recipeDetail = response.meals?.firstOrNull()?.let { mealDto ->
                val detail = mealDto.toRecipeDetail(availableIngredients)
                recipeDetailsCache[cacheKey] = detail
                detail
            }
            Result.success(recipeDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRecipesWithSmartMatching(
        availableIngredients: List<String>,
        availableEquipment: List<String>
    ): Result<Pair<List<Recipe>, List<Recipe>>> {
        if (availableIngredients.isEmpty()) {
            return Result.success(Pair(emptyList(), emptyList()))
        }

        return try {
            val allRecipes = mutableSetOf<String>()
            val recipeMap = mutableMapOf<String, Recipe>()

            for (ingredient in availableIngredients.take(1)) {
                val response = apiService.searchRecipesByIngredient(ingredient)
                response.meals?.take(8)?.forEach { mealDto ->
                    if (!allRecipes.contains(mealDto.id)) {
                        allRecipes.add(mealDto.id)
                        recipeMap[mealDto.id] = mealDto.toRecipe()
                    }
                }
            }

            val fullMatchRecipes = mutableListOf<Recipe>()
            val partialMatchRecipes = mutableListOf<Recipe>()

            val availableIngredientSet = availableIngredients.map { it.lowercase().trim() }.toSet()
            val availableEquipmentSet = availableEquipment.map { it.lowercase().trim() }.toSet()

            val recipesWithDetails = mutableListOf<Triple<Recipe, RecipeDetail, Int>>()
            
            for (recipeId in allRecipes.take(8)) {
                val detailResult = getRecipeDetails(recipeId, availableIngredients)
                if (detailResult.isSuccess) {
                    val detail = detailResult.getOrNull()
                    val recipe = recipeMap[recipeId]
                    if (detail != null && recipe != null) {
                        val equipmentMatchScore = if (availableEquipmentSet.isNotEmpty() && detail.requiredEquipment.isNotEmpty()) {
                            val requiredEquipmentSet = detail.requiredEquipment.map { it.lowercase().trim() }.toSet()
                            val matchingEquipment = requiredEquipmentSet.count { it in availableEquipmentSet }
                            matchingEquipment
                        } else {
                            0
                        }
                        
                        recipesWithDetails.add(Triple(recipe, detail, equipmentMatchScore))
                    }
                }
            }

            val sortedRecipes = recipesWithDetails.sortedWith(
                compareBy<Triple<Recipe, RecipeDetail, Int>> { it.second.missingIngredients.size }
                    .thenByDescending { it.third }
            )

            for ((recipe, detail, equipmentScore) in sortedRecipes) {
                val updatedRecipe = recipe.copy(
                    matchType = if (detail.missingIngredients.isEmpty()) RecipeMatchType.FULL_MATCH else RecipeMatchType.PARTIAL_MATCH,
                    missingIngredients = detail.missingIngredients
                )

                if (detail.missingIngredients.isEmpty()) {
                    fullMatchRecipes.add(updatedRecipe)
                } else {
                    partialMatchRecipes.add(updatedRecipe)
                }
            }

            Result.success(Pair(fullMatchRecipes, partialMatchRecipes))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    override fun getAllEquipment(): Flow<List<Equipment>> = equipmentDao.getAllEquipment()
    override suspend fun addEquipment(equipment: Equipment) = equipmentDao.insertEquipment(equipment)
    override suspend fun deleteEquipment(equipment: Equipment) = equipmentDao.deleteEquipment(equipment)
}