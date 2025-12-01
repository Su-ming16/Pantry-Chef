package com.example.pantrychef.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrychef.data.model.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE name = :name")
    suspend fun getEquipmentByName(name: String): Equipment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: Equipment)

    @Delete
    suspend fun deleteEquipment(equipment: Equipment)

    @Query("DELETE FROM equipment WHERE name = :name")
    suspend fun deleteEquipmentByName(name: String)
}

