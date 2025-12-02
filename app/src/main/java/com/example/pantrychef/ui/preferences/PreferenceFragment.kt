package com.example.pantrychef.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrychef.PantryChefApplication
import com.example.pantrychef.data.model.DietaryPreference
import com.example.pantrychef.databinding.FragmentPreferenceBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import kotlinx.coroutines.launch

class PreferenceFragment : Fragment() {
    
    private var _binding: FragmentPreferenceBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PreferenceViewModel by viewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }
    
    private lateinit var adapter: PreferenceAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observePreference()
    }
    
    private fun setupRecyclerView() {
        adapter = PreferenceAdapter(
            preferences = DietaryPreference.values().toList(),
            onPreferenceSelected = { preference ->
                viewModel.updatePreference(preference)
            }
        )
        
        binding.rvPreferences.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPreferences.adapter = adapter
    }
    
    private fun observePreference() {
        lifecycleScope.launch {
            viewModel.currentPreference.collect { preference ->
                adapter.updateSelection(preference)
                updateHeader(preference)
            }
        }
    }
    
    private fun updateHeader(preference: DietaryPreference) {
        if (preference == DietaryPreference.NONE) {
            binding.tvCurrentPreference.text = "No dietary preference selected"
            binding.tvCurrentDescription.text = "All recipes will be shown"
        } else {
            binding.tvCurrentPreference.text = "Current: ${preference.icon} ${preference.displayName}"
            binding.tvCurrentDescription.text = preference.description
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

