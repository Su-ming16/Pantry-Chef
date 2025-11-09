package com.example.pantrychef.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)

