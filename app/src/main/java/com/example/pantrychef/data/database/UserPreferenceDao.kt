package com.example.pantrychef.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrychef.data.model.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreference(): Flow<UserPreference?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePreference(preference: UserPreference)
    
    @Query("SELECT dietaryPreference FROM user_preferences WHERE id = 1")
    suspend fun getDietaryPreference(): String?
}
