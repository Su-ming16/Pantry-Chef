package com.example.pantrychef.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val onItemClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class RecipeViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(recipe: Recipe) {
            binding.tvRecipeName.text = recipe.name
            binding.tvCategory.text = recipe.category ?: ""
            binding.tvArea.text = recipe.area ?: ""
            
            if (recipe.matchType == com.example.pantrychef.data.model.RecipeMatchType.PARTIAL_MATCH) {
                binding.tvRecommendationTag.visibility = android.view.View.VISIBLE
            } else {
                binding.tvRecommendationTag.visibility = android.view.View.GONE
            }
            
            if (!recipe.thumbnail.isNullOrBlank()) {
                Glide.with(binding.root)
                    .load(recipe.thumbnail)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivThumbnail)
            }
            
            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }
    
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem.id == newItem.id
            }
            
            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }
        }
    }
}

