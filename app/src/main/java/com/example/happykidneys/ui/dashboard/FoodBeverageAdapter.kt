package com.example.happykidneys.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.happykidneys.R
import com.example.happykidneys.data.database.entities.FoodBeverage
import com.example.happykidneys.databinding.ItemFoodBeverageBinding
import java.util.*

class FoodBeverageAdapter(
    private val onItemClick: (FoodBeverage) -> Unit
) : ListAdapter<FoodBeverage, FoodBeverageAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFoodBeverageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFoodBeverageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodBeverage) {
            val locale = Locale.getDefault().language
            val name = when (locale) {
                "ar" -> item.nameAr
                "fr" -> item.nameFr
                else -> item.name
            }

            binding.tvName.text = name
            binding.tvWaterContent.text = "${item.waterPercentage.toInt()}% water"
            binding.tvServing.text = item.servingSize
            binding.tvAmount.text = String.format("%.2fL", item.waterInLiters)

            val iconRes = when (item.category) {
                "fruit" -> R.drawable.ic_apple
                "vegetable" -> R.drawable.ic_restaurant
                "beverage" -> R.drawable.ic_local_drink
                else -> R.drawable.ic_restaurant
            }
            binding.ivIcon.setImageResource(iconRes)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<FoodBeverage>() {
        override fun areItemsTheSame(oldItem: FoodBeverage, newItem: FoodBeverage): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodBeverage, newItem: FoodBeverage): Boolean =
            oldItem == newItem
    }
}