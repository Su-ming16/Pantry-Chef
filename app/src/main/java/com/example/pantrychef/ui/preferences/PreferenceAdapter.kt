package com.example.pantrychef.ui.preferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrychef.R
import com.example.pantrychef.data.model.DietaryPreference

class PreferenceAdapter(
    private val preferences: List<DietaryPreference>,
    private val onPreferenceSelected: (DietaryPreference) -> Unit
) : RecyclerView.Adapter<PreferenceAdapter.PreferenceViewHolder>() {
    
    private var selectedPreference: DietaryPreference = DietaryPreference.NONE
    
    inner class PreferenceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.rbPreference)
        val tvName: TextView = view.findViewById(R.id.tvPreferenceName)
        val tvDescription: TextView = view.findViewById(R.id.tvPreferenceDescription)
        val tvIcon: TextView = view.findViewById(R.id.tvPreferenceIcon)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preference, parent, false)
        return PreferenceViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        val preference = preferences[position]
        
        holder.tvIcon.text = preference.icon
        holder.tvName.text = preference.displayName
        holder.tvDescription.text = preference.description
        holder.radioButton.isChecked = (preference == selectedPreference)
        
        holder.itemView.setOnClickListener {
            val oldPosition = preferences.indexOf(selectedPreference)
            selectedPreference = preference
            notifyItemChanged(oldPosition)
            notifyItemChanged(position)
            onPreferenceSelected(preference)
        }
        
        holder.radioButton.setOnClickListener {
            holder.itemView.performClick()
        }
    }
    
    override fun getItemCount() = preferences.size
    
    fun updateSelection(preference: DietaryPreference) {
        val oldPosition = preferences.indexOf(selectedPreference)
        selectedPreference = preference
        val newPosition = preferences.indexOf(preference)
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }
}
