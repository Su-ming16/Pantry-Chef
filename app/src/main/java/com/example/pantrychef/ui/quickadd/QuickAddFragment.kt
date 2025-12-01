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
import com.example.pantrychef.databinding.FragmentQuickAddBinding
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

        val categories = listOf(
            IngredientCategory(
                name = "🥬 Vegetables",
                items = listOf(
                    "Tomato", "Onion", "Carrot", "Lettuce", "Cabbage",
                    "Cucumber", "Bell Pepper", "Broccoli", "Spinach", "Potato",
                    "Celery", "Garlic", "Ginger", "Mushroom", "Corn",
                    "Eggplant", "Zucchini", "Pumpkin", "Radish", "Bean Sprouts"
                )
            ),
            IngredientCategory(
                name = "🍎 Fruits",
                items = listOf(
                    "Apple", "Banana", "Orange", "Strawberry", "Grape",
                    "Watermelon", "Lemon", "Lime", "Mango", "Pineapple",
                    "Blueberry", "Peach", "Pear", "Cherry", "Kiwi",
                    "Avocado", "Papaya", "Coconut", "Plum", "Raspberry"
                )
            ),
            IngredientCategory(
                name = "🍗 Meat & Poultry",
                items = listOf(
                    "Chicken", "Beef", "Pork", "Lamb", "Turkey",
                    "Chicken Breast", "Ground Beef", "Bacon", "Sausage",
                    "Ham", "Duck", "Veal"
                )
            ),
            IngredientCategory(
                name = "🐟 Seafood",
                items = listOf(
                    "Salmon", "Tuna", "Shrimp", "Fish", "Cod",
                    "Crab", "Lobster", "Squid", "Tilapia", "Sardines",
                    "Clams", "Mussels", "Scallops"
                )
            ),
            IngredientCategory(
                name = "🧀 Dairy & Eggs",
                items = listOf(
                    "Milk", "Cheese", "Butter", "Yogurt", "Cream",
                    "Egg", "Cheddar", "Mozzarella", "Sour Cream",
                    "Cream Cheese", "Parmesan", "Feta"
                )
            ),
            IngredientCategory(
                name = "🌾 Grains & Pasta",
                items = listOf(
                    "Rice", "Pasta", "Bread", "Flour", "Noodles",
                    "Quinoa", "Oats", "Spaghetti", "Couscous", "Barley",
                    "Tortilla", "Pita Bread", "Rice Noodles"
                )
            ),
            IngredientCategory(
                name = "🫘 Beans & Legumes",
                items = listOf(
                    "Black Beans", "Kidney Beans", "Chickpeas", "Lentils",
                    "Peas", "Tofu", "Soybeans", "Green Beans", "Peanuts"
                )
            ),
            IngredientCategory(
                name = "🌰 Nuts & Seeds",
                items = listOf(
                    "Almonds", "Walnuts", "Cashews", "Peanuts", "Pistachios",
                    "Sunflower Seeds", "Chia Seeds", "Sesame Seeds", "Pumpkin Seeds"
                )
            ),
            IngredientCategory(
                name = "🧂 Seasonings & Spices",
                items = listOf(
                    "Salt", "Pepper", "Garlic Powder", "Onion Powder", "Paprika",
                    "Cumin", "Cinnamon", "Oregano", "Basil", "Thyme",
                    "Rosemary", "Chili Powder", "Turmeric", "Ginger Powder"
                )
            ),
            IngredientCategory(
                name = "🫗 Oils & Sauces",
                items = listOf(
                    "Olive Oil", "Vegetable Oil", "Soy Sauce", "Vinegar",
                    "Ketchup", "Mayonnaise", "Mustard", "Hot Sauce",
                    "BBQ Sauce", "Sesame Oil", "Honey", "Sugar"
                )
            )
        )

        adapter = IngredientCategoryAdapter(
            categories = categories,
            selectedIngredients = selectedIngredients,
            onSelectionChanged = {
                updateAddButton()
            }
        )

        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        updateAddButton()

        binding.btnAdd.setOnClickListener {
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

    private fun updateAddButton() {
        binding.btnAdd.text = "Add ${selectedIngredients.size} ingredient(s)"
        binding.btnAdd.isEnabled = selectedIngredients.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
