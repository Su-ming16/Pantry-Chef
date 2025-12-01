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
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

