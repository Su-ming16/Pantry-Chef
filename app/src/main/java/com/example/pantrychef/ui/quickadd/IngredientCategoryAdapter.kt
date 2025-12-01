package com.example.pantrychef.ui.quickadd

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
        val tvToggle: TextView = view.findViewById(R.id.tvToggle)
        val tvSelectAll: TextView = view.findViewById(R.id.tvSelectAll)
        val rvItems: RecyclerView = view.findViewById(R.id.rvItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_add_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        
        holder.tvCategory.text = category.name
        
        val itemsAdapter = IngredientItemAdapter(
            items = category.items,
            selectedIngredients = selectedIngredients,
            onToggle = { _, _ ->
                onSelectionChanged()
                notifyItemChanged(position)
            }
        )
        
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = itemsAdapter
        holder.rvItems.visibility = if (category.isExpanded) View.VISIBLE else View.GONE
        
        holder.tvToggle.text = if (category.isExpanded) "▼" else "▶"
        
        val selectedCount = category.items.count { selectedIngredients.contains(it) }
        if (selectedCount == category.items.size) {
            holder.tvSelectAll.text = "Deselect All"
        } else {
            holder.tvSelectAll.text = "Select All (${category.items.size})"
        }
        
        holder.tvCategory.setOnClickListener {
            category.isExpanded = !category.isExpanded
            notifyItemChanged(position)
        }
        
        holder.tvToggle.setOnClickListener {
            category.isExpanded = !category.isExpanded
            notifyItemChanged(position)
        }
        
        holder.tvSelectAll.setOnClickListener {
            if (selectedCount == category.items.size) {
                category.items.forEach { selectedIngredients.remove(it) }
            } else {
                category.items.forEach { selectedIngredients.add(it) }
            }
            onSelectionChanged()
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = categories.size
}

class IngredientItemAdapter(
    private val items: List<String>,
    private val selectedIngredients: MutableSet<String>,
    private val onToggle: (String, Boolean) -> Unit
) : RecyclerView.Adapter<IngredientItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.cbItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_add_ingredient, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        
        holder.checkbox.text = item
        holder.checkbox.isChecked = selectedIngredients.contains(item)
        
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIngredients.add(item)
            } else {
                selectedIngredients.remove(item)
            }
            onToggle(item, isChecked)
        }
        
        holder.itemView.setOnClickListener {
            holder.checkbox.isChecked = !holder.checkbox.isChecked
        }
    }

    override fun getItemCount() = items.size
}

