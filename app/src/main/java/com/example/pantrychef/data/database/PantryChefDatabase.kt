package com.example.pantrychef.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.UserPreference

@Database(entities = [Ingredient::class, FavoriteRecipe::class, UserPreference::class], version = 4, exportSchema = false)
abstract class PantryChefDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun userPreferenceDao(): UserPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: PantryChefDatabase? = null

        fun getDatabase(context: Context): PantryChefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryChefDatabase::class.java,
                    "pantry_chef_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
