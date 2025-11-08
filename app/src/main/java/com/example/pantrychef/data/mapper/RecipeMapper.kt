package com.example.pantrychef.data.mapper

import com.example.pantrychef.data.model.IngredientItem
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.data.model.RecipeDetail
import com.example.pantrychef.data.network.dto.MealDto

// This function converts a MealDto (from a search list) into our clean Recipe domain model.
fun MealDto.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        thumbnail = this.imageUrl
    )
}

// This is the more complex mapper. It converts a MealDto (from a details lookup)
// into our clean RecipeDetail domain model.
fun MealDto.toRecipeDetail(): RecipeDetail {
    val ingredientItems = mutableListOf<IngredientItem>()

    // This loop is the magic. It intelligently goes through all 20 possible
    // ingredient/measure fields from the DTO and combines them into a clean list.
    for (i in 1..20) {
        val ingredientName = getIngredient(this, i)
        val measure = getMeasure(this, i)

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

// Helper functions to get ingredient/measure by number using reflection.
// This avoids a giant if/else or when statement.
private fun getIngredient(meal: MealDto, index: Int): String? {
    return meal::class.members.find { it.name == "ingredient$index" }?.call(meal) as? String
}

private fun getMeasure(meal: MealDto, index: Int): String? {
    return meal::class.members.find { it.name == "measure$index" }?.call(meal) as? String
}