package com.example.pantrychef.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pantrychef.data.model.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<Ingredient>>
    
    @Query("SELECT * FROM ingredients WHERE name LIKE :query || '%' ORDER BY name ASC")
    fun searchIngredients(query: String): Flow<List<Ingredient>>
    
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient): Long
    
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)
    
    @Query("DELETE FROM ingredients WHERE id = :id")
    suspend fun deleteIngredientById(id: Long)
}

