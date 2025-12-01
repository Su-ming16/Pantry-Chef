package com.example.pantrychef.ui.pantry

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
import com.example.pantrychef.R
import com.example.pantrychef.databinding.FragmentMyPantryBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import kotlinx.coroutines.launch

class MyPantryFragment : Fragment() {

    private var _binding: FragmentMyPantryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPantryViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }

    private lateinit var adapter: IngredientAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView

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

        stateView = (binding.root as ViewGroup).createStateView(binding.rvIngredients)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ingredients.collect { ingredients ->
                if (ingredients.isEmpty()) {
                    stateView.showEmpty(getString(R.string.empty_ingredients))
                } else {
                    stateView.showContent()
                    adapter.submitList(ingredients)
                }
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

        binding.btnCamera.setOnClickListener {
            val action = MyPantryFragmentDirections.actionMyPantryFragmentToQuickAddFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}