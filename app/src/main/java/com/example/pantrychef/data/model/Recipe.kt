package com.example.pantrychef.data.model

enum class RecipeMatchType {
    FULL_MATCH,      // 完全匹配：所有食材都有
    PARTIAL_MATCH    // 部分匹配：里斯推荐（需要额外食材）
}

data class Recipe(
    val id: String,
    val name: String,
    val thumbnail: String? = null,
    val category: String? = null,
    val area: String? = null,
    val matchType: RecipeMatchType = RecipeMatchType.FULL_MATCH,
    val missingIngredients: List<String> = emptyList(),
    val allIngredients: List<String> = emptyList()
)

data class RecipeDetail(
    val id: String,
    val name: String,
    val image: String? = null,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    val ingredients: List<IngredientItem> = emptyList(),
    val requiredEquipment: List<String> = emptyList(),
    val missingIngredients: List<String> = emptyList()
)

data class IngredientItem(
    val name: String,
    val measure: String? = null,
    val isAvailable: Boolean = true
)

