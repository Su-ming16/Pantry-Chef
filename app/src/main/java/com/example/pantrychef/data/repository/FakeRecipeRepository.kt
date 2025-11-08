package com.example.pantrychef.data.repository

import com.example.pantrychef.data.model.IngredientItem
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeRecipeRepository : RecipeRepository {
    private val favoritesMap = mutableMapOf<String, Recipe>()
    private val fakeRecipes = listOf(
        Recipe(
            id = "52772",
            name = "Teriyaki Chicken Casserole",
            thumbnail = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            category = "Chicken",
            area = "Japanese"
        ),
        Recipe(
            id = "52850",
            name = "Chicken & mushroom Hotpot",
            thumbnail = "https://www.themealdb.com/images/media/meals/uuuspp1511297945.jpg",
            category = "Chicken",
            area = "British"
        ),
        Recipe(
            id = "53013",
            name = "Big Mac",
            thumbnail = "https://www.themealdb.com/images/media/meals/urzj1d1587670726.jpg",
            category = "Beef",
            area = "American"
        ),
        Recipe(
            id = "52928",
            name = "Beef and Oyster pie",
            thumbnail = "https://www.themealdb.com/images/media/meals/wrssvt1511556563.jpg",
            category = "Beef",
            area = "British"
        ),
        Recipe(
            id = "52977",
            name = "Corba",
            thumbnail = "https://www.themealdb.com/images/media/meals/58oia61564916529.jpg",
            category = "Side",
            area = "Turkish"
        )
    )
    
    private val fakeDetails = mapOf(
        "52772" to RecipeDetail(
            id = "52772",
            name = "Teriyaki Chicken Casserole",
            image = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            category = "Chicken",
            area = "Japanese",
            instructions = "Preheat oven to 350° F. Spray a 9x13-inch baking pan with nonstick cooking spray.\n\n" +
                    "Combine soy sauce, C&H® Golden Brown Sugar, water, garlic, ginger, and red pepper flakes in a small bowl and set aside.\n\n" +
                    "Toss the chicken pieces in the cornstarch and coat evenly. Shake off excess cornstarch.\n\n" +
                    "Heat the oil in a large skillet over medium-high heat. Add chicken and cook until browned.\n\n" +
                    "Place chicken in prepared pan. Pour sauce over chicken and combine. Add vegetables.\n\n" +
                    "Bake for 15 minutes. Add pineapple and bake for additional 5 minutes.",
            ingredients = listOf(
                IngredientItem("chicken breast", "3"),
                IngredientItem("soy sauce", "3/4 cup"),
                IngredientItem("brown sugar", "1/4 cup"),
                IngredientItem("ground ginger", "1/2 tsp"),
                IngredientItem("minced garlic", "1/2 tsp"),
                IngredientItem("cornstarch", "4 Tbsp"),
                IngredientItem("chicken broth", "1/2 cup"),
                IngredientItem("rice", "2 cups")
            )
        ),
        "52850" to RecipeDetail(
            id = "52850",
            name = "Chicken & mushroom Hotpot",
            image = "https://www.themealdb.com/images/media/meals/uuuspp1511297945.jpg",
            category = "Chicken",
            area = "British",
            instructions = "Heat the oil in a large pan and fry the chicken pieces for 2-3 mins until golden. " +
                    "You may need to do this in 2 batches. Remove and set aside.\n\n" +
                    "Fry the onion for 3-4 mins until softened. Add the mushrooms and cook for 2 mins. " +
                    "Return the chicken to the pan and pour in the stock. Cover and simmer for 30 mins.\n\n" +
                    "Stir in the crème fraîche and tarragon, then season. Turn off the heat and add the cooked rice. " +
                    "Stir through and serve.",
            ingredients = listOf(
                IngredientItem("chicken", "1kg"),
                IngredientItem("onion", "1"),
                IngredientItem("mushrooms", "250g"),
                IngredientItem("chicken stock", "600ml"),
                IngredientItem("crème fraîche", "200ml"),
                IngredientItem("tarragon", "2 tbsp"),
                IngredientItem("rice", "300g")
            )
        )
    )
    
    override fun searchRecipesByIngredient(ingredient: String): Flow<Result<List<Recipe>>> = flow {
        delay(800)
        if (ingredient.isBlank()) {
            emit(Result.success(emptyList()))
        } else {
            val filtered = fakeRecipes.filter { 
                it.name.contains(ingredient, ignoreCase = true) || 
                it.category?.contains(ingredient, ignoreCase = true) == true
            }
            emit(Result.success(if (filtered.isEmpty()) fakeRecipes else filtered))
        }
    }
    
    override fun getRecipeDetail(recipeId: String): Flow<Result<RecipeDetail>> = flow {
        delay(500)
        val detail = fakeDetails[recipeId]
        if (detail != null) {
            emit(Result.success(detail))
        } else {
            val recipe = fakeRecipes.find { it.id == recipeId }
            if (recipe != null) {
                emit(Result.success(
                    RecipeDetail(
                        id = recipe.id,
                        name = recipe.name,
                        image = recipe.thumbnail,
                        category = recipe.category,
                        area = recipe.area,
                        instructions = "This is a placeholder recipe. Full instructions will be available in the real implementation.",
                        ingredients = emptyList()
                    )
                ))
            } else {
                emit(Result.failure(Exception("Recipe not found")))
            }
        }
    }
    
    override suspend fun addFavorite(recipe: Recipe) {
        delay(200)
        favoritesMap[recipe.id] = recipe
    }
    
    override suspend fun removeFavorite(recipeId: String) {
        delay(200)
        favoritesMap.remove(recipeId)
    }
    
    override fun getAllFavorites(): Flow<List<Recipe>> = flow {
        delay(300)
        emit(favoritesMap.values.toList())
    }
    
    override fun isFavorite(recipeId: String): Flow<Boolean> = flow {
        delay(100)
        emit(favoritesMap.containsKey(recipeId))
    }
}

