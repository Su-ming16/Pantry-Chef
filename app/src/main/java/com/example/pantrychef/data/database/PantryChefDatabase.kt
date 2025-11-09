package com.example.pantrychef.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient

@Database(entities = [Ingredient::class, FavoriteRecipe::class], version = 1, exportSchema = false)
abstract class PantryChefDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao

    companion object {
        @Volatile
        private var INSTANCE: PantryChefDatabase? = null

        fun getDatabase(context: Context): PantryChefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryChefDatabase::class.java,
                    "pantry_chef_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

