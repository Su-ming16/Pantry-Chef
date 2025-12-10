package com.example.pantrychef.data.repository

import com.example.pantrychef.data.database.FavoriteRecipeDao
import com.example.pantrychef.data.database.IngredientDao
import com.example.pantrychef.data.database.UserPreferenceDao
import com.example.pantrychef.data.mapper.toRecipe
import com.example.pantrychef.data.mapper.toRecipeDetail
import com.example.pantrychef.data.model.DietaryPreference
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.model.RecipeMatchType
import com.example.pantrychef.data.model.UserPreference
import com.example.pantrychef.data.network.TheMealDbApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class RecipeRepositoryImpl(
    private val ingredientDao: IngredientDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val userPreferenceDao: UserPreferenceDao,
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
        availableIngredients: List<String>
    ): Result<Pair<List<Recipe>, List<Recipe>>> {
        if (availableIngredients.isEmpty()) {
            return Result.success(Pair(emptyList(), emptyList()))
        }

        return try {
            coroutineScope {
                val allRecipes = mutableSetOf<String>()
                val recipeMap = mutableMapOf<String, Recipe>()

                val currentPreference = userPreferenceDao.getUserPreference().firstOrNull()?.dietaryPreference
                val preference = if (currentPreference != null) {
                    DietaryPreference.fromString(currentPreference)
                } else {
                    DietaryPreference.NONE
                }

                android.util.Log.d("RecipeRepository", "Smart matching with preference: $preference")

                // Parallel API calls for ingredients
                when (preference) {
                    DietaryPreference.VEGETARIAN -> {
                        android.util.Log.d("RecipeRepository", "Using Vegetarian category search")
                        val response = apiService.searchRecipesByCategory("Vegetarian")
                        response.meals?.take(15)?.forEach { mealDto ->
                            if (!allRecipes.contains(mealDto.id)) {
                                allRecipes.add(mealDto.id)
                                recipeMap[mealDto.id] = mealDto.toRecipe()
                            }
                        }
                    }
                    DietaryPreference.FITNESS -> {
                        android.util.Log.d("RecipeRepository", "Searching protein-rich ingredients in parallel")
                        val proteinIngredients = listOf("chicken", "fish", "egg", "beef").filter { protein ->
                            availableIngredients.any { it.lowercase().contains(protein) }
                        }.ifEmpty { listOf("chicken") }
                        
                        // Parallel search for protein ingredients
                        val searchJobs = proteinIngredients.take(2).map { ingredient ->
                            async {
                                try {
                                    apiService.searchRecipesByIngredient(ingredient)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }
                        searchJobs.awaitAll().filterNotNull().forEach { response ->
                            response.meals?.take(8)?.forEach { mealDto ->
                                synchronized(allRecipes) {
                                    if (!allRecipes.contains(mealDto.id)) {
                                        allRecipes.add(mealDto.id)
                                        recipeMap[mealDto.id] = mealDto.toRecipe()
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        android.util.Log.d("RecipeRepository", "Parallel search with ingredients: $availableIngredients")
                        
                        // Parallel search for all ingredients (max 3 for speed)
                        val searchJobs = availableIngredients.take(3).map { ingredient ->
                            async {
                                android.util.Log.d("RecipeRepository", "Searching ingredient: $ingredient")
                                try {
                                    apiService.searchRecipesByIngredient(ingredient)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }
                        searchJobs.awaitAll().filterNotNull().forEach { response ->
                            response.meals?.take(10)?.forEach { mealDto ->
                                synchronized(allRecipes) {
                                    if (!allRecipes.contains(mealDto.id)) {
                                        allRecipes.add(mealDto.id)
                                        recipeMap[mealDto.id] = mealDto.toRecipe()
                                    }
                                }
                            }
                        }
                        android.util.Log.d("RecipeRepository", "Total recipes found: ${allRecipes.size}")
                    }
                }

                val fullMatchRecipes = mutableListOf<Recipe>()
                val partialMatchRecipes = mutableListOf<Recipe>()
                
                // Seasonings/condiments that don't count as "main" ingredients
                val seasonings = setOf(
                    "salt", "pepper", "sugar", "olive oil", "vegetable oil", "oil", "butter",
                    "garlic", "onion", "ginger", "soy sauce", "vinegar", "ketchup", "mustard",
                    "mayonnaise", "hot sauce", "worcestershire sauce", "fish sauce",
                    "cumin", "paprika", "oregano", "basil", "thyme", "rosemary", "cinnamon",
                    "chili powder", "curry powder", "turmeric", "bay leaf", "parsley",
                    "black pepper", "white pepper", "cayenne", "nutmeg", "coriander",
                    "sesame oil", "honey", "maple syrup", "lemon juice", "lime juice",
                    "garlic powder", "onion powder", "italian seasoning", "herbs"
                )

                data class RecipeWithScore(
                    val recipe: Recipe,
                    val detail: RecipeDetail,
                    val ownedMainIngredients: Int,
                    val totalMainIngredients: Int,
                    val matchPercentage: Double
                )
                
                // Limit recipes to process for speed (max 15)
                val recipesToProcess = allRecipes.take(15)
                
                // Parallel fetch of recipe details
                val detailJobs = recipesToProcess.map { recipeId ->
                    async {
                        try {
                            val detailResult = getRecipeDetails(recipeId, availableIngredients)
                            if (detailResult.isSuccess) {
                                val detail = detailResult.getOrNull()
                                val recipe = recipeMap[recipeId]
                                if (detail != null && recipe != null) {
                                    Triple(recipeId, recipe, detail)
                                } else null
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                
                val recipesWithDetails = detailJobs.awaitAll().filterNotNull().mapNotNull { (_, recipe, detail) ->
                    val totalIngredients = detail.ingredients.size
                    val missingCount = detail.missingIngredients.size
                    val ownedCount = totalIngredients - missingCount
                    val matchPercentage = if (totalIngredients > 0) {
                        (ownedCount.toDouble() / totalIngredients.toDouble()) * 100
                    } else {
                        0.0
                    }
                    
                    // Calculate owned MAIN ingredients (excluding seasonings)
                    val allIngredientNames = detail.ingredients.map { it.name.lowercase().trim() }
                    val missingIngredientNames = detail.missingIngredients.map { it.lowercase().trim() }
                    
                    val mainIngredients = allIngredientNames.filter { ing ->
                        !seasonings.any { seasoning -> ing.contains(seasoning) || seasoning.contains(ing) }
                    }
                    val ownedMainIngredients = mainIngredients.count { ing ->
                        !missingIngredientNames.any { missing -> ing == missing || ing.contains(missing) || missing.contains(ing) }
                    }
                    
                    android.util.Log.d("RecipeRepository", "Recipe: ${recipe.name}, OwnedMain: $ownedMainIngredients/${mainIngredients.size}, Total: $ownedCount/$totalIngredients")
                    
                    // Filter out recipes with 0 owned main ingredients
                    if (ownedMainIngredients == 0 && mainIngredients.isNotEmpty()) {
                        android.util.Log.d("RecipeRepository", "Filtering out: ${recipe.name} (no main ingredients owned)")
                        null
                    } else {
                        RecipeWithScore(recipe, detail, ownedMainIngredients, mainIngredients.size, matchPercentage)
                    }
                }

                // Sort by: 1) owned main ingredients count (descending), 2) match percentage
                val sortedRecipes = recipesWithDetails.sortedWith(
                    compareByDescending<RecipeWithScore> { it.ownedMainIngredients }
                        .thenByDescending { it.matchPercentage }
                )

                for (recipeWithScore in sortedRecipes) {
                    val updatedRecipe = recipeWithScore.recipe.copy(
                        matchType = if (recipeWithScore.detail.missingIngredients.isEmpty()) RecipeMatchType.FULL_MATCH else RecipeMatchType.PARTIAL_MATCH,
                        missingIngredients = recipeWithScore.detail.missingIngredients,
                        allIngredients = recipeWithScore.detail.ingredients.map { it.name }
                    )

                    if (recipeWithScore.detail.missingIngredients.isEmpty()) {
                        fullMatchRecipes.add(updatedRecipe)
                    } else {
                        partialMatchRecipes.add(updatedRecipe)
                    }
                }

                val filteredFullMatch = applyPreferenceFilter(fullMatchRecipes)
                val filteredPartialMatch = applyPreferenceFilter(partialMatchRecipes)

                Result.success(Pair(filteredFullMatch, filteredPartialMatch))
            }
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
    
    private suspend fun applyPreferenceFilter(recipes: List<Recipe>): List<Recipe> {
        val preferenceString = userPreferenceDao.getUserPreference().firstOrNull()?.dietaryPreference 
        val preference = if (preferenceString != null) {
            DietaryPreference.fromString(preferenceString)
        } else {
            DietaryPreference.NONE
        }
        
        if (preference == DietaryPreference.NONE) {
            return recipes
        }
        
        return recipes
            .filter { recipe -> shouldIncludeRecipe(recipe, preference) }
            .sortedByDescending { recipe -> calculatePreferenceScore(recipe, preference) }
    }
    
    private fun shouldIncludeRecipe(recipe: Recipe, preference: DietaryPreference): Boolean {
        val recipeName = recipe.name.lowercase()
        val allIngredients = recipe.allIngredients.map { it.lowercase() }
        
        return when (preference) {
            DietaryPreference.VEGETARIAN -> {
                val meatKeywords = listOf("chicken", "beef", "pork", "fish", "meat", "lamb", "turkey", "bacon", "sausage", "shrimp", "salmon", "tuna", "seafood", "prawn", "crab", "lobster", "anchovy", "cod", "ham", "duck", "goose")
                val hasMeat = meatKeywords.any { keyword -> 
                    recipeName.contains(keyword) || allIngredients.any { it.contains(keyword) }
                }
                android.util.Log.d("RecipeFilter", "Recipe: ${recipe.name}, hasMeat: $hasMeat, ingredients: $allIngredients")
                !hasMeat
            }
            DietaryPreference.WEIGHT_LOSS -> {
                val avoidKeywords = listOf("fried", "deep-fried", "cream", "chocolate", "cake", "dessert", "butter")
                !avoidKeywords.any { keyword -> recipeName.contains(keyword) }
            }
            DietaryPreference.KID_FRIENDLY -> {
                val avoidKeywords = listOf("spicy", "chili", "hot pepper", "curry", "wasabi", "jalapeño")
                !avoidKeywords.any { keyword -> recipeName.contains(keyword) }
            }
            else -> true
        }
    }
    
    private fun calculatePreferenceScore(recipe: Recipe, preference: DietaryPreference): Int {
        var score = 100
        val recipeName = recipe.name.lowercase()
        val ingredients = recipe.missingIngredients.map { it.lowercase() }
        
        when (preference) {
            DietaryPreference.FITNESS -> {
                if (recipeName.contains("chicken") || ingredients.any { it.contains("chicken") }) score += 40
                if (recipeName.contains("beef") || ingredients.any { it.contains("beef") }) score += 35
                if (recipeName.contains("fish") || recipeName.contains("salmon") || recipeName.contains("tuna")) score += 45
                if (recipeName.contains("egg") || ingredients.any { it.contains("egg") }) score += 25
                if (recipeName.contains("grilled") || recipeName.contains("baked") || recipeName.contains("roasted")) score += 30
                if (recipeName.contains("fried") || recipeName.contains("deep-fried")) score -= 60
                if (recipeName.contains("protein") || recipeName.contains("lean")) score += 20
            }
            
            DietaryPreference.WEIGHT_LOSS -> {
                if (recipeName.contains("salad") || recipeName.contains("soup")) score += 40
                if (recipeName.contains("vegetable") || recipeName.contains("veggie")) score += 30
                if (recipeName.contains("steamed") || recipeName.contains("boiled")) score += 25
                if (recipeName.contains("light") || recipeName.contains("low-cal")) score += 20
                if (recipe.missingIngredients.size <= 3) score += 15
            }
            
            DietaryPreference.VEGETARIAN -> {
                if (recipeName.contains("vegetable") || recipeName.contains("veggie")) score += 40
                if (recipeName.contains("salad") || recipeName.contains("pasta")) score += 30
                if (recipeName.contains("tofu") || recipeName.contains("bean")) score += 35
                if (ingredients.any { it.contains("vegetable") || it.contains("tomato") || it.contains("onion") }) score += 20
            }
            
            DietaryPreference.QUICK_EASY -> {
                score += (10 - recipe.missingIngredients.size) * 10
                if (recipeName.contains("quick") || recipeName.contains("easy") || recipeName.contains("simple")) score += 40
                if (recipeName.contains("one-pot") || recipeName.contains("sheet pan")) score += 30
            }
            
            DietaryPreference.KID_FRIENDLY -> {
                if (recipeName.contains("pasta") || recipeName.contains("noodle")) score += 40
                if (recipeName.contains("chicken") || recipeName.contains("rice")) score += 35
                if (recipeName.contains("cheese") || recipeName.contains("pizza")) score += 30
                if (recipeName.contains("mild") || recipeName.contains("sweet")) score += 20
            }
            
            DietaryPreference.NONE -> {}
        }
        
        score -= recipe.missingIngredients.size * 8
        
        return score.coerceAtLeast(0)
    }
    
    override fun getUserPreference(): Flow<UserPreference?> = userPreferenceDao.getUserPreference()
    
    override suspend fun updateUserPreference(preference: UserPreference) {
        userPreferenceDao.updatePreference(preference)
    }
}
