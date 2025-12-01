package com.example.pantrychef.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrychef.data.model.Equipment
import com.example.pantrychef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _equipment = MutableStateFlow<List<Equipment>>(emptyList())
    val equipment: StateFlow<List<Equipment>> = _equipment.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllEquipment()
                .collect { equipmentList -> 
                    _equipment.value = equipmentList.sortedBy { it.name.lowercase() }
                }
        }
    }

    fun addEquipment(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.addEquipment(Equipment(name = name.trim()))
            }
        }
    }

    fun deleteEquipment(equipment: Equipment) {
        viewModelScope.launch {
            repository.deleteEquipment(equipment)
        }
    }
}

