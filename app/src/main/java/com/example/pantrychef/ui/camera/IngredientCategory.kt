package com.example.pantrychef.ui.camera

data class IngredientCategory(
    val categoryName: String,
    val suggestions: List<String>,
    var isExpanded: Boolean = true
)

