package com.example.pantrychef.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrychef.PantryChefApplication
import com.example.pantrychef.databinding.FragmentIngredientConfirmBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.pantry.MyPantryViewModel
import kotlinx.coroutines.launch

class IngredientConfirmFragment : Fragment() {

    private var _binding: FragmentIngredientConfirmBinding? = null
    private val binding get() = _binding!!
    
    private val pantryViewModel: MyPantryViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }
    
    private val sharedViewModel: SharedRecognitionViewModel by activityViewModels()

    private lateinit var adapter: IngredientCategoryAdapter
    private val selectedIngredients = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            sharedViewModel.categories.collect { categories ->
                if (categories.isNotEmpty()) {
                    setupRecyclerView(categories)
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            lifecycleScope.launch {
                for (ingredient in selectedIngredients) {
                    pantryViewModel.addIngredient(ingredient)
                }
                sharedViewModel.clear()
                findNavController().popBackStack(
                    com.example.pantrychef.R.id.myPantryFragment, 
                    false
                )
            }
        }

        binding.btnCancel.setOnClickListener {
            sharedViewModel.clear()
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView(categories: List<IngredientCategory>) {
        adapter = IngredientCategoryAdapter(
            categories = categories,
            selectedIngredients = selectedIngredients,
            onSelectionChanged = {
                updateConfirmButton()
            }
        )

        binding.rvRecognizedIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecognizedIngredients.adapter = adapter

        updateConfirmButton()
    }

    private fun updateConfirmButton() {
        binding.btnConfirm.text = "Add ${selectedIngredients.size} ingredient(s)"
        binding.btnConfirm.isEnabled = selectedIngredients.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

