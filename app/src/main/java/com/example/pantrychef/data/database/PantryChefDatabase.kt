package com.example.pantrychef.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pantrychef.data.model.Equipment
import com.example.pantrychef.data.model.FavoriteRecipe
import com.example.pantrychef.data.model.Ingredient
import com.example.pantrychef.data.model.UserPreference

@Database(entities = [Ingredient::class, FavoriteRecipe::class, Equipment::class, UserPreference::class], version = 3, exportSchema = false)
abstract class PantryChefDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun equipmentDao(): EquipmentDao
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

