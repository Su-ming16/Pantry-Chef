package com.example.pantrychef.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrychef.data.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {
    @Query("SELECT * FROM favorite_recipes ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRecipe>>
    
    @Query("SELECT * FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun getFavoriteById(recipeId: String): FavoriteRecipe?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteRecipe)
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteRecipe)
    
    @Query("DELETE FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun deleteFavoriteById(recipeId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE recipeId = :recipeId)")
    fun isFavorite(recipeId: String): Flow<Boolean>
}

