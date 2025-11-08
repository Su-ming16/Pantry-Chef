package com.example.pantrychef.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrychef.databinding.FragmentMyFavoritesBinding
import com.example.pantrychef.ui.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import com.example.pantrychef.ui.discover.RecipeAdapter
import kotlinx.coroutines.launch

class MyFavoritesFragment : Fragment() {
    
    private var _binding: FragmentMyFavoritesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MyFavoritesViewModel by viewModels { ViewModelFactory() }
    
    private lateinit var adapter: RecipeAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = RecipeAdapter { recipe ->
            val action = MyFavoritesFragmentDirections.actionMyFavoritesFragmentToRecipeDetailFragment(
                recipeId = recipe.id
            )
            findNavController().navigate(action)
        }
        
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        
        stateView = (binding.root as ViewGroup).createStateView(binding.rvFavorites)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { favorites ->
                if (favorites.isEmpty()) {
                    stateView.showEmpty("No favorite recipes yet")
                } else {
                    stateView.showContent()
                    adapter.submitList(favorites)
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

