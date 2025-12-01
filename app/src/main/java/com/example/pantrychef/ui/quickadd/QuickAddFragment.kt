package com.example.pantrychef.ui.quickadd

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
import com.example.pantrychef.databinding.FragmentQuickAddBinding
import com.example.pantrychef.ui.camera.IngredientCategory
import com.example.pantrychef.ui.camera.IngredientCategoryAdapter
import com.example.pantrychef.ui.common.ViewModelFactory
import com.example.pantrychef.ui.pantry.MyPantryViewModel
import kotlinx.coroutines.launch

class QuickAddFragment : Fragment() {

    private var _binding: FragmentQuickAddBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MyPantryViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as PantryChefApplication).repository)
    }

    private lateinit var adapter: IngredientCategoryAdapter
    private val selectedIngredients = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = getIngredientCategories()
        setupRecyclerView(categories)

        binding.btnConfirm.setOnClickListener {
            lifecycleScope.launch {
                for (ingredient in selectedIngredients) {
                    viewModel.addIngredient(ingredient)
                }
                findNavController().popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
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

        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredients.adapter = adapter

        updateConfirmButton()
    }

    private fun updateConfirmButton() {
        binding.btnConfirm.text = "Add ${selectedIngredients.size} ingredient(s)"
        binding.btnConfirm.isEnabled = selectedIngredients.isNotEmpty()
    }

    private fun getIngredientCategories(): List<IngredientCategory> {
        return listOf(
            IngredientCategory(
                categoryName = "Vegetables",
                suggestions = listOf(
                    "Tomato", "Onion", "Carrot", "Lettuce", "Cabbage",
                    "Cucumber", "Bell Pepper", "Broccoli", "Spinach", "Potato",
                    "Celery", "Garlic", "Ginger", "Mushroom", "Corn", "Eggplant"
                )
            ),
            IngredientCategory(
                categoryName = "Fruits",
                suggestions = listOf(
                    "Apple", "Banana", "Orange", "Strawberry", "Grape",
                    "Watermelon", "Lemon", "Lime", "Mango", "Pineapple",
                    "Blueberry", "Peach", "Pear", "Cherry", "Kiwi"
                )
            ),
            IngredientCategory(
                categoryName = "Meat & Poultry",
                suggestions = listOf(
                    "Chicken", "Beef", "Pork", "Lamb", "Turkey",
                    "Chicken Breast", "Ground Beef", "Bacon", "Sausage", "Ham"
                )
            ),
            IngredientCategory(
                categoryName = "Seafood",
                suggestions = listOf(
                    "Salmon", "Tuna", "Shrimp", "Fish", "Cod",
                    "Crab", "Lobster", "Squid", "Tilapia"
                )
            ),
            IngredientCategory(
                categoryName = "Dairy & Eggs",
                suggestions = listOf(
                    "Milk", "Cheese", "Butter", "Yogurt", "Cream",
                    "Cheddar", "Mozzarella", "Sour Cream", "Cream Cheese", "Egg"
                )
            ),
            IngredientCategory(
                categoryName = "Grains & Pasta",
                suggestions = listOf(
                    "Rice", "Pasta", "Bread", "Flour", "Noodles",
                    "Quinoa", "Oats", "Spaghetti", "Couscous"
                )
            ),
            IngredientCategory(
                categoryName = "Condiments & Sauces",
                suggestions = listOf(
                    "Soy Sauce", "Olive Oil", "Vinegar", "Ketchup", "Mustard",
                    "Mayonnaise", "Hot Sauce", "Honey", "Salt", "Pepper"
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

