package com.example.pantrychef.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient

@Database(
    entities = [Ingredient::class, FavoriteRecipe::class],
    version = 1,
    exportSchema = false
)
abstract class PantryChefDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}

