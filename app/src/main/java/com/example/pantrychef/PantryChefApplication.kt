package com.example.pantrychef

import android.app.Application
import com.example.pantrychef.data.database.PantryChefDatabase
import com.example.pantrychef.data.network.ApiClient
import com.example.pantrychef.data.repository.RecipeRepository
import com.example.pantrychef.data.repository.RecipeRepositoryImpl

class PantryChefApplication : Application() {
    // Create the database instance lazily
    private val database by lazy { PantryChefDatabase.getDatabase(this) }

    // Create the repository instance lazily, providing it with the necessary DAOs and API service
    val repository: RecipeRepository by lazy {
        RecipeRepositoryImpl(
            ingredientDao = database.ingredientDao(),
            favoriteRecipeDao = database.favoriteRecipeDao(),
            apiService = ApiClient.apiService
        )
    }
}