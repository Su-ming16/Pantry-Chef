package com.example.pantrychef.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrychef.data.model.Equipment
import com.example.pantrychef.databinding.ItemEquipmentBinding

class EquipmentAdapter(
    private val onDeleteClick: (Equipment) -> Unit
) : ListAdapter<Equipment, EquipmentAdapter.EquipmentViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val binding = ItemEquipmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EquipmentViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class EquipmentViewHolder(
        private val binding: ItemEquipmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(equipment: Equipment) {
            binding.tvEquipmentName.text = equipment.name
            binding.btnDelete.setOnClickListener {
                onDeleteClick(equipment)
            }
        }
    }
    
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Equipment>() {
            override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem.name == newItem.name
            }
            
            override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem == newItem
            }
        }
    }
}

