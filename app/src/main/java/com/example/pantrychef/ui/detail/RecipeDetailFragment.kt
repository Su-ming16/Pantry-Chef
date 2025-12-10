package com.example.pantrychef.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pantrychef.PantryChefApplication
import com.example.pantrychef.databinding.FragmentRecipeDetailBinding
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.common.createStateView
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {
    
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RecipeDetailViewModel by viewModels(ownerProducer = { this }) {
        val app = (requireActivity().application as? PantryChefApplication)
            ?: throw IllegalStateException("Application is not PantryChefApplication")
        ViewModelFactory(app.repository)
    }
    
    private lateinit var adapter: IngredientDetailAdapter
    private lateinit var stateView: com.example.pantrychef.ui.common.StateView
    private var lastLoadedRecipeId: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onResume() {
        super.onResume()
        val recipeId = arguments?.getString("recipeId")
        android.util.Log.d("RecipeDetail", "onResume: recipeId=$recipeId, lastLoaded=$lastLoadedRecipeId")
        if (!recipeId.isNullOrBlank() && recipeId != lastLoadedRecipeId) {
            android.util.Log.d("RecipeDetail", "Loading new recipe in onResume: $recipeId")
            lastLoadedRecipeId = recipeId
            viewModel.loadRecipeDetail(recipeId)
        }
    }
    
    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        android.util.Log.d("RecipeDetail", "setArguments called: recipeId=${args?.getString("recipeId")}")
        lastLoadedRecipeId = null
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up back button click listener
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
        
        // Handle system back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            }
        )
        
        try {
            adapter = IngredientDetailAdapter()
            binding.rvIngredients.layoutManager = LinearLayoutManager(context ?: return)
            binding.rvIngredients.adapter = adapter
            
            val scrollView = binding.root as? androidx.core.widget.NestedScrollView
            if (scrollView != null) {
                val contentContainer = scrollView.getChildAt(0) as? ViewGroup
                if (contentContainer != null) {
                    stateView = contentContainer.createStateView(contentContainer)
                } else {
                    stateView = (binding.root as ViewGroup).createStateView(binding.root)
                }
            } else {
                stateView = (binding.root as ViewGroup).createStateView(binding.root)
            }
            
            val recipeId = arguments?.getString("recipeId") ?: savedInstanceState?.getString("recipeId")
            android.util.Log.d("RecipeDetail", "onViewCreated: recipeId=$recipeId, lastLoaded=$lastLoadedRecipeId")
            if (recipeId.isNullOrBlank()) {
                stateView.showError {
                    if (isAdded && activity != null) {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
                return
            }
            
            if (recipeId != lastLoadedRecipeId) {
                lastLoadedRecipeId = recipeId
                viewModel.loadRecipeDetail(recipeId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (::stateView.isInitialized) {
                stateView.showError {
                    if (isAdded && activity != null) {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
            }
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.uiState.collect { state ->
                    if (!isAdded) return@collect
                    
                    android.util.Log.d("RecipeDetail", "UI State changed: ${state::class.simpleName}")
                    when (state) {
                        is DetailUiState.Loading -> {
                            stateView.showLoading()
                        }
                        is DetailUiState.Success -> {
                            android.util.Log.d("RecipeDetail", "Displaying detail: name=${state.detail.name}, id=${state.detail.id}")
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
                                val context = context ?: return@collect
                                Glide.with(context)
                                    .load(detail.image)
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .error(android.R.drawable.ic_menu_gallery)
                                    .into(binding.ivRecipeImage)
                            } else {
                                binding.ivRecipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
                            }
                            
                            if (detail.missingIngredients.isNotEmpty()) {
                                binding.cardMissingIngredients.visibility = android.view.View.VISIBLE
                                binding.tvMissingIngredients.text = detail.missingIngredients.joinToString(", ")
                            } else {
                                binding.cardMissingIngredients.visibility = android.view.View.GONE
                            }
                            
                            if (detail.requiredEquipment.isNotEmpty()) {
                                binding.tvRequiredEquipment.text = detail.requiredEquipment.joinToString(", ")
                            } else {
                                binding.tvRequiredEquipment.text = "No special equipment required"
                            }
                            
                            adapter.submitList(detail.ingredients)
                        }
                        is DetailUiState.Error -> {
                            android.util.Log.e("RecipeDetail", "Error state: ${state.message}")
                            stateView.showError {
                                viewModel.retry()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (isAdded && ::stateView.isInitialized) {
                    stateView.showError {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.isFavorite.collect { isFavorite ->
                    if (!isAdded) return@collect
                    android.util.Log.d("RecipeDetail", "isFavorite changed: $isFavorite, updating button icon")
                    
                    binding.btnFavorite.setImageResource(
                        if (isFavorite) android.R.drawable.btn_star_big_on
                        else android.R.drawable.btn_star_big_off
                    )
                    
                    val colorInt = if (isFavorite) {
                        android.graphics.Color.parseColor("#FFD700")
                    } else {
                        android.graphics.Color.GRAY
                    }
                    binding.btnFavorite.setColorFilter(colorInt)
                    
                    android.util.Log.d("RecipeDetail", "Button icon updated, isFavorite=$isFavorite, color=${if(isFavorite) "gold" else "gray"}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        binding.btnFavorite.setOnClickListener {
            android.util.Log.d("RecipeDetail", "Favorite button clicked")
            val currentRecipeId = arguments?.getString("recipeId")
            if (currentRecipeId.isNullOrBlank()) {
                android.util.Log.e("RecipeDetail", "RecipeId is blank, cannot toggle favorite")
                return@setOnClickListener
            }
            val recipeName = binding.tvRecipeName.text.toString()
            val image = viewModel.uiState.value.let {
                if (it is DetailUiState.Success) it.detail.image else null
            }
            android.util.Log.d("RecipeDetail", "Toggling favorite: id=$currentRecipeId, name=$recipeName")
            viewModel.toggleFavorite(currentRecipeId, recipeName, image)
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        arguments?.getString("recipeId")?.let {
            outState.putString("recipeId", it)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun navigateBack() {
        if (isAdded) {
            findNavController().popBackStack()
        }
    }
}

