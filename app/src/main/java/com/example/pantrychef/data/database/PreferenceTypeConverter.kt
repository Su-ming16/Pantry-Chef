package com.example.pantrychef.data.database

import androidx.room.TypeConverter
import com.example.pantrychef.data.model.DietaryPreference

class PreferenceTypeConverter {
    
    @TypeConverter
    fun fromDietaryPreference(preference: DietaryPreference): String {
        return preference.name
    }
    
    @TypeConverter
    fun toDietaryPreference(value: String): DietaryPreference {
        return try {
            DietaryPreference.valueOf(value)
        } catch (e: IllegalArgumentException) {
            DietaryPreference.NONE
        }
    }
}
