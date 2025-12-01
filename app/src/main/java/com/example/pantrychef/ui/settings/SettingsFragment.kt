package com.example.pantrychef.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrychef.PantryChefApplication
import com.example.pantrychef.databinding.FragmentSettingsBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }

    private lateinit var adapter: EquipmentAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EquipmentAdapter { equipment ->
            viewModel.deleteEquipment(equipment)
        }

        binding.rvEquipment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEquipment.adapter = adapter

        stateView = (binding.root as ViewGroup).createStateView(binding.rvEquipment)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.equipment.collect { equipmentList ->
                if (equipmentList.isEmpty()) {
                    stateView.showEmpty("No equipment added. Add your kitchen equipment to get better recipe recommendations.")
                } else {
                    stateView.showContent()
                    adapter.submitList(equipmentList)
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etEquipment.text?.toString()?.trim()
            if (!name.isNullOrBlank()) {
                viewModel.addEquipment(name)
                binding.etEquipment.text?.clear()
            }
        }

        binding.etEquipment.setOnEditorActionListener { _, _, _ ->
            val name = binding.etEquipment.text?.toString()?.trim()
            if (!name.isNullOrBlank()) {
                viewModel.addEquipment(name)
                binding.etEquipment.text?.clear()
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

