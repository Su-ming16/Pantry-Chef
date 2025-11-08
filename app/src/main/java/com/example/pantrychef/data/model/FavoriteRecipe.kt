package com.example.pantrychef.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey
    val recipeId: String,
    val name: String,
    val thumbnail: String? = null,
    val category: String? = null,
    val area: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

