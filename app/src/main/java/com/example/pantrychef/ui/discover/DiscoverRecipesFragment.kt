package com.example.pantrychef.ui.discover

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
import com.example.pantrychef.data.model.DietaryPreference
import com.example.pantrychef.data.model.Recipe
import com.example.pantrychef.databinding.FragmentDiscoverRecipesBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import kotlinx.coroutines.launch

class DiscoverRecipesFragment : Fragment() {
    
    private var _binding: FragmentDiscoverRecipesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DiscoverRecipesViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }
    
    private lateinit var adapter: RecipeAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = RecipeAdapter { recipe ->
            android.util.Log.d("DiscoverRecipes", "Clicking recipe: id=${recipe.id}, name=${recipe.name}")
            val action = DiscoverRecipesFragmentDirections.actionDiscoverRecipesFragmentToRecipeDetailFragment(
                recipeId = recipe.id
            )
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(findNavController().graph.startDestinationId, false)
                .build()
            findNavController().navigate(action, navOptions)
        }
        
        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.adapter = adapter
        
        stateView = (binding.root as ViewGroup).createStateView(binding.rvRecipes)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DiscoverUiState.Loading -> {
                        stateView.showLoading()
                    }
                    is DiscoverUiState.Success -> {
                        stateView.showContent()
                        adapter.submitList(state.recipes)
                    }
                    is DiscoverUiState.SuccessWithRecommendations -> {
                        stateView.showContent()
                        val allRecipes = mutableListOf<Recipe>()
                        if (state.fullMatchRecipes.isNotEmpty()) {
                            allRecipes.addAll(state.fullMatchRecipes)
                        }
                        if (state.recommendedRecipes.isNotEmpty()) {
                            allRecipes.addAll(state.recommendedRecipes)
                        }
                        adapter.submitList(allRecipes)
                    }
                    is DiscoverUiState.Error -> {
                        stateView.showError {
                            viewModel.searchWithFirstAvailableIngredient()
                        }
                    }
                    is DiscoverUiState.Empty -> {
                        stateView.showEmpty("Please add ingredients first, then click \"Cook Now\" button")
                    }
                }
            }
        }
        
        binding.btnCookNow.setOnClickListener {
            viewModel.searchWithFirstAvailableIngredient()
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentPreference.collect { preference ->
                updatePreferenceIndicator(preference)
            }
        }
    }
    
    private fun updatePreferenceIndicator(preference: com.example.pantrychef.data.model.DietaryPreference) {
        if (preference == com.example.pantrychef.data.model.DietaryPreference.NONE) {
            binding.cardPreferenceIndicator.visibility = View.GONE
        } else {
            binding.cardPreferenceIndicator.visibility = View.VISIBLE
            val message = when (preference) {
                com.example.pantrychef.data.model.DietaryPreference.FITNESS -> 
                    "${preference.icon} Fitness Mode Active - Showing high-protein recipes"
                com.example.pantrychef.data.model.DietaryPreference.WEIGHT_LOSS -> 
                    "${preference.icon} Weight Loss Mode - Showing low-calorie, light meals"
                com.example.pantrychef.data.model.DietaryPreference.VEGETARIAN -> 
                    "${preference.icon} Vegetarian Mode - Showing meat-free recipes"
                com.example.pantrychef.data.model.DietaryPreference.QUICK_EASY -> 
                    "${preference.icon} Quick & Easy Mode - Showing simple recipes"
                com.example.pantrychef.data.model.DietaryPreference.KID_FRIENDLY -> 
                    "${preference.icon} Kid Friendly Mode - Showing mild, familiar recipes"
                else -> ""
            }
            binding.tvPreferenceIndicator.text = message
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

