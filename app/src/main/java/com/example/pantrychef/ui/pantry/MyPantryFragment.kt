package com.example.pantrychef.ui.pantry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
// IMPORTANT: Make sure this import is correct. It might be ...ViewModelFactory from a different package.
// Let's use the one we defined in ui.common
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.PantryChefApplication
import com.example.pantrychef.databinding.FragmentMyPantryBinding
// Assuming IngredientAdapter exists. If not, this is Role C's task.
// import com.example.pantrychef.ui.pantry.IngredientAdapter
import kotlinx.coroutines.launch

class MyPantryFragment : Fragment() {

    private var _binding: FragmentMyPantryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPantryViewModel by viewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }

    private lateinit var adapter: IngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPantryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IngredientAdapter { ingredient ->
            viewModel.deleteIngredient(ingredient)
        }

        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredients.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ingredients.collect { ingredients ->
                adapter.submitList(ingredients)
            }
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etIngredient.text?.toString()?.trim()
            if (!name.isNullOrBlank()) {
                viewModel.addIngredient(name)
                binding.etIngredient.text?.clear()
            }
        }

        binding.etIngredient.setOnEditorActionListener { _, _, _ ->
            val name = binding.etIngredient.text?.toString()?.trim()
            if (!name.isNullOrBlank()) {
                viewModel.addIngredient(name)
                binding.etIngredient.text?.clear()
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}