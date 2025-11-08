package com.example.pantrychef.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient

// This annotation tells Room about the entities (tables) and the database version.
@Database(entities = [Ingredient::class, FavoriteRecipe::class], version = 1, exportSchema = false)
abstract class PantryChefDatabase : RoomDatabase() {

    // These abstract functions connect the database to your DAOs. Room will generate the implementation.
    // THIS IS LIKELY ONE OF THE MISSING PARTS.
    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao

    // The companion object allows us to create a singleton instance of the database.
    // This ensures we only have one database connection open at a time.
    // THIS IS THE OTHER LIKELY MISSING PART.
    companion object {
        @Volatile
        private var INSTANCE: PantryChefDatabase? = null

        fun getDatabase(context: Context): PantryChefDatabase {
            // Return the existing instance, or create a new one if it doesn't exist.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryChefDatabase::class.java,
                    "pantry_chef_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

