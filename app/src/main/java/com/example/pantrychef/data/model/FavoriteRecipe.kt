package com.example.pantrychef.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    // ADDED: A field to store the timestamp. We'll default it to the current time.
    val addedAt: Long = System.currentTimeMillis()
)

