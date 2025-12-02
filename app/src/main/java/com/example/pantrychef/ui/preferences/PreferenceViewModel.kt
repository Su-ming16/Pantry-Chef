package com.example.pantrychef.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.DietaryPreference
import com.example.pantrychef.data.model.UserPreference
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferenceViewModel(private val repository: RecipeRepository) : ViewModel() {
    
    private val _currentPreference = MutableStateFlow(DietaryPreference.NONE)
    val currentPreference: StateFlow<DietaryPreference> = _currentPreference.asStateFlow()
    
    init {
        loadPreference()
    }
    
    private fun loadPreference() {
        viewModelScope.launch {
            repository.getUserPreference().collect { preference ->
                _currentPreference.value = if (preference != null) {
                    DietaryPreference.fromString(preference.dietaryPreference)
                } else {
                    DietaryPreference.NONE
                }
            }
        }
    }
    
    fun updatePreference(preference: DietaryPreference) {
        viewModelScope.launch {
            repository.updateUserPreference(UserPreference(dietaryPreference = preference.name))
            _currentPreference.value = preference
        }
    }
}

