package com.example.pantrychef.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pantrychef.databinding.FragmentRecipeDetailBinding
import com.example.pantrychef.ui.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {
    
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RecipeDetailViewModel by viewModels { ViewModelFactory() }
    
    private lateinit var adapter: IngredientDetailAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val recipeId = arguments?.getString("recipeId") ?: ""
        
        adapter = IngredientDetailAdapter()
        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredients.adapter = adapter
        
        stateView = (binding.root as ViewGroup).createStateView(binding.root)
        
        viewModel.loadRecipeDetail(recipeId)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DetailUiState.Loading -> {
                        stateView.showLoading()
                    }
                    is DetailUiState.Success -> {
                        stateView.showContent()
                        val detail = state.detail
                        binding.tvRecipeName.text = detail.name
                        binding.tvCategoryArea.text = buildString {
                            if (!detail.category.isNullOrBlank()) {
                                append(detail.category)
                            }
                            if (!detail.area.isNullOrBlank()) {
                                if (isNotEmpty()) append(" • ")
                                append(detail.area)
                            }
                        }
                        binding.tvInstructions.text = detail.instructions ?: ""
                        
                        if (!detail.image.isNullOrBlank()) {
                            Glide.with(requireContext())
                                .load(detail.image)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.ivRecipeImage)
                        }
                        
                        adapter.submitList(detail.ingredients)
                    }
                    is DetailUiState.Error -> {
                        stateView.showError {
                            viewModel.retry()
                        }
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.btnFavorite.setImageResource(
                    if (isFavorite) android.R.drawable.btn_star_big_on
                    else android.R.drawable.btn_star_big_off
                )
            }
        }
        
        binding.btnFavorite.setOnClickListener {
            val recipeId = arguments?.getString("recipeId") ?: return@setOnClickListener
            val recipeName = binding.tvRecipeName.text.toString()
            val image = viewModel.uiState.value.let {
                if (it is DetailUiState.Success) it.detail.image else null
            }
            viewModel.toggleFavorite(recipeId, recipeName, image)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

