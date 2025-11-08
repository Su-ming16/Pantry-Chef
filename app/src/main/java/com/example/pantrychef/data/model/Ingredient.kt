package com.example.pantrychef.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val name: String
)
