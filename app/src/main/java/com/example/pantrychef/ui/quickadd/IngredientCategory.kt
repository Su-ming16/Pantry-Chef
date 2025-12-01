package com.example.pantrychef.ui.quickadd

data class IngredientCategory(
    val name: String,
    val items: List<String>,
    var isExpanded: Boolean = true
)

