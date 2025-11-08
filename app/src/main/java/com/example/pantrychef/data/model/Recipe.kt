package com.example.pantrychef.data.model

data class Recipe(
    val id: String,
    val name: String,
    val thumbnail: String? = null,
    val category: String? = null,
    val area: String? = null
)

data class RecipeDetail(
    val id: String,
    val name: String,
    val image: String? = null,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    val ingredients: List<IngredientItem> = emptyList()
)

data class IngredientItem(
    val name: String,
    val measure: String? = null
)

