package com.example.pantrychef.data.mapper

import com.example.pantrychef.data.model.IngredientItem
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.network.dto.MealDto

fun MealDto.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        thumbnail = this.imageUrl
    )
}

fun MealDto.toRecipeDetail(): RecipeDetail {
    val ingredientItems = mutableListOf<IngredientItem>()

    val ingredients = listOf(
        ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
        ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
        ingredient11, ingredient12, ingredient13, ingredient14, ingredient15,
        ingredient16, ingredient17, ingredient18, ingredient19, ingredient20
    )
    
    val measures = listOf(
        measure1, measure2, measure3, measure4, measure5,
        measure6, measure7, measure8, measure9, measure10,
        measure11, measure12, measure13, measure14, measure15,
        measure16, measure17, measure18, measure19, measure20
    )

    for (i in ingredients.indices) {
        val ingredientName = ingredients[i]
        val measure = measures[i]
        
        if (!ingredientName.isNullOrBlank()) {
            ingredientItems.add(IngredientItem(name = ingredientName, measure = measure))
        }
    }

    return RecipeDetail(
        id = this.id,
        name = this.name,
        image = this.imageUrl,
        category = this.category,
        area = this.area,
        instructions = this.instructions,
        ingredients = ingredientItems
    )
}