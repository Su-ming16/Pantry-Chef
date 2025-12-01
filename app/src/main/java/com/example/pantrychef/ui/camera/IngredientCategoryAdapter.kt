package com.example.pantrychef.ui.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrychef.R

class IngredientCategoryAdapter(
    private val categories: List<IngredientCategory>,
    private val selectedIngredients: MutableSet<String>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<IngredientCategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvSelectAll: TextView = view.findViewById(R.id.tvSelectAll)
        val rvSuggestions: RecyclerView = view.findViewById(R.id.rvSuggestions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        
        holder.tvCategory.text = "${category.categoryName} (${category.suggestions.size} suggestions)"
        
        val suggestionsAdapter = SuggestionAdapter(
            suggestions = category.suggestions,
            selectedIngredients = selectedIngredients,
            onToggle = { ingredient, isSelected ->
                if (isSelected) {
                    selectedIngredients.add(ingredient)
                } else {
                    selectedIngredients.remove(ingredient)
                }
                onSelectionChanged()
                notifyItemChanged(position)
            }
        )
        
        holder.rvSuggestions.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvSuggestions.adapter = suggestionsAdapter
        
        val selectedCount = category.suggestions.count { selectedIngredients.contains(it) }
        if (selectedCount == category.suggestions.size) {
            holder.tvSelectAll.text = "Deselect All"
        } else {
            holder.tvSelectAll.text = "Select All"
        }
        
        holder.tvSelectAll.setOnClickListener {
            if (selectedCount == category.suggestions.size) {
                category.suggestions.forEach { selectedIngredients.remove(it) }
            } else {
                category.suggestions.forEach { selectedIngredients.add(it) }
            }
            onSelectionChanged()
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = categories.size
}

class SuggestionAdapter(
    private val suggestions: List<String>,
    private val selectedIngredients: MutableSet<String>,
    private val onToggle: (String, Boolean) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.cbIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        
        holder.checkbox.text = suggestion
        holder.checkbox.isChecked = selectedIngredients.contains(suggestion)
        
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onToggle(suggestion, isChecked)
        }
        
        holder.itemView.setOnClickListener {
            holder.checkbox.isChecked = !holder.checkbox.isChecked
        }
    }

    override fun getItemCount() = suggestions.size
}

