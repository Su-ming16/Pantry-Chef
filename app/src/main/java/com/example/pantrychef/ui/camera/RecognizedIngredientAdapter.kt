package com.example.pantrychef.ui.camera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrychef.databinding.ItemRecognizedIngredientBinding

class RecognizedIngredientAdapter(
    private val initialItems: List<String>,
    private val onItemToggle: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecognizedIngredientAdapter.IngredientViewHolder>() {

    private val selectedStates = initialItems.associateWith { true }.toMutableMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemRecognizedIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(initialItems[position])
    }

    override fun getItemCount() = initialItems.size

    inner class IngredientViewHolder(
        private val binding: ItemRecognizedIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: String) {
            binding.tvIngredientName.text = ingredient
            binding.checkboxSelect.isChecked = selectedStates[ingredient] ?: true

            binding.checkboxSelect.setOnCheckedChangeListener { _, isChecked ->
                selectedStates[ingredient] = isChecked
                onItemToggle(ingredient, isChecked)
            }

            binding.root.setOnClickListener {
                binding.checkboxSelect.isChecked = !binding.checkboxSelect.isChecked
            }
        }
    }
}

