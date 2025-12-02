package com.example.pantrychef.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey
    val id: Int = 1,
    val dietaryPreference: String = DietaryPreference.NONE.name
)
