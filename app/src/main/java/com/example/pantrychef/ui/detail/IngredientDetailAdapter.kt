package com.example.pantrychef.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrychef.data.model.IngredientItem
import com.example.pantrychef.databinding.ItemIngredientDetailBinding

class IngredientDetailAdapter : ListAdapter<IngredientItem, IngredientDetailAdapter.IngredientViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class IngredientViewHolder(
        private val binding: ItemIngredientDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: IngredientItem) {
            binding.tvIngredientName.text = item.name
            binding.tvMeasure.text = item.measure ?: ""
            
            if (!item.isAvailable) {
                binding.tvIngredientName.setTextColor(android.graphics.Color.parseColor("#E65100"))
                binding.tvIngredientName.text = "⚠️ ${item.name}"
                binding.tvIngredientName.textSize = 14f
            } else {
                binding.tvIngredientName.setTextColor(android.graphics.Color.parseColor("#212121"))
                binding.tvIngredientName.text = item.name
                binding.tvIngredientName.textSize = 14f
            }
        }
    }
    
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<IngredientItem>() {
            override fun areItemsTheSame(oldItem: IngredientItem, newItem: IngredientItem): Boolean {
                return oldItem.name == newItem.name
            }
            
            override fun areContentsTheSame(oldItem: IngredientItem, newItem: IngredientItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

