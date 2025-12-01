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

fun MealDto.toRecipeDetail(availableIngredients: List<String> = emptyList()): RecipeDetail {
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

    val basicIngredients = setOf(
        "salt", "pepper", "water", "oil", "olive oil", "vegetable oil",
        "black pepper", "white pepper", "sea salt", "sugar", "butter",
        "flour", "cornstarch", "baking powder", "baking soda", "vanilla",
        "vanilla extract", "garlic powder", "onion powder"
    )

    val availableIngredientSet = availableIngredients.map { it.lowercase().trim() }
    val missingIngredients = mutableListOf<String>()
    
    android.util.Log.d("RecipeMapper", "toRecipeDetail: recipe=${this.name}, availableIngredients=$availableIngredients")

    for (i in ingredients.indices) {
        val ingredientName = ingredients[i]
        val measure = measures[i]
        
        if (!ingredientName.isNullOrBlank()) {
            val normalizedName = ingredientName.lowercase().trim()
            val isBasicIngredient = basicIngredients.contains(normalizedName)
            
            val isAvailable = isBasicIngredient || availableIngredientSet.any { available ->
                val matches = normalizedName.contains(available) || available.contains(normalizedName)
                if (matches) {
                    android.util.Log.d("RecipeMapper", "  Match: '$ingredientName' matched with '$available'")
                }
                matches
            }
            
            if (!isAvailable && !isBasicIngredient) {
                missingIngredients.add(ingredientName)
                android.util.Log.d("RecipeMapper", "  Missing: '$ingredientName'")
            }
            
            ingredientItems.add(IngredientItem(
                name = ingredientName,
                measure = measure,
                isAvailable = isAvailable
            ))
        }
    }
    
    android.util.Log.d("RecipeMapper", "Total missing: ${missingIngredients.size} - $missingIngredients")

    val requiredEquipment = extractEquipmentFromInstructions(this.instructions)

    return RecipeDetail(
        id = this.id,
        name = this.name,
        image = this.imageUrl,
        category = this.category,
        area = this.area,
        instructions = this.instructions,
        ingredients = ingredientItems,
        requiredEquipment = requiredEquipment,
        missingIngredients = missingIngredients
    )
}

private fun extractEquipmentFromInstructions(instructions: String?): List<String> {
    if (instructions.isNullOrBlank()) return emptyList()
    
    val equipmentKeywords = listOf(
        "oven", "microwave", "stove", "pan", "pot", "skillet", "wok",
        "blender", "food processor", "mixer", "whisk", "spatula",
        "knife", "cutting board", "grill", "barbecue", "bbq",
        "steamer", "pressure cooker", "slow cooker", "crock pot",
        "air fryer", "toaster", "toaster oven", "broiler",
        "saucepan", "frying pan", "griddle", "roasting pan"
    )
    
    val foundEquipment = mutableSetOf<String>()
    val lowerInstructions = instructions.lowercase()
    
    for (keyword in equipmentKeywords) {
        if (lowerInstructions.contains(keyword)) {
            foundEquipment.add(keyword.replaceFirstChar { it.uppercaseChar() })
        }
    }
    
    return foundEquipment.toList()
}