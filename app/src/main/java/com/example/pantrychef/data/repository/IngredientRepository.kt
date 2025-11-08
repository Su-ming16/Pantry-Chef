package com.example.pantrychef.data.repository

import com.example.pantrychef.data.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    fun getAllIngredients(): Flow<List<Ingredient>>
    fun searchIngredients(query: String): Flow<List<Ingredient>>
    suspend fun addIngredient(name: String)
    suspend fun deleteIngredient(ingredient: Ingredient)
}

