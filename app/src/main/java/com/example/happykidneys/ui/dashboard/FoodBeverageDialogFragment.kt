package com.example.happykidneys.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.FoodBeverage
import com.example.happykidneys.data.repository.FoodBeverageRepository
import com.example.happykidneys.databinding.FragmentFoodBeverageSelectionBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.util.*

class FoodBeverageDialogFragment(
    private val onItemSelected: (FoodBeverage) -> Unit
) : DialogFragment() {

    private var _binding: FragmentFoodBeverageSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: FoodBeverageRepository
    private lateinit var adapter: FoodBeverageAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), com.google.android.material.R.style.Theme_MaterialComponents_Dialog).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBeverageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = FoodBeverageRepository(database.foodBeverageDao())

        setupRecyclerView()
        setupSearch()
        setupCategoryFilters()
        loadAllItems()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            (resources.displayMetrics.heightPixels * 0.85).toInt()
        )
    }

    private fun setupRecyclerView() {
        adapter = FoodBeverageAdapter { item ->
            onItemSelected(item)
            dismiss()
        }
        binding.rvItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FoodBeverageDialogFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadAllItems()
                } else {
                    searchItems(newText)
                }
                return true
            }
        })
    }

    private fun setupCategoryFilters() {
        val categories = listOf(
            "all" to "All",
            "beverage" to "Beverages",
            "fruit" to "Fruits",
            "vegetable" to "Vegetables",
            "meal" to "Meals"
        )

        categories.forEach { (key, label) ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isChecked = key == "all"
                setOnClickListener {
                    if (key == "all") {
                        loadAllItems()
                    } else {
                        loadByCategory(key)
                    }
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun loadAllItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getAllItems().collect { items ->
                adapter.submitList(items)
            }
        }
    }

    private fun loadByCategory(category: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getItemsByCategory(category).collect { items ->
                adapter.submitList(items)
            }
        }
    }

    private fun searchItems(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.searchItems(query).collect { items ->
                adapter.submitList(items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}