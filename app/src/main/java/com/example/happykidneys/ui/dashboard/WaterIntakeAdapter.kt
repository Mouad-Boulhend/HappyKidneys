package com.example.happykidneys.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.happykidneys.R
import com.example.happykidneys.data.database.entities.WaterIntake
import com.example.happykidneys.databinding.ItemWaterIntakeBinding
import java.text.SimpleDateFormat
import java.util.*

class WaterIntakeAdapter(
    private val onDeleteClick: (WaterIntake) -> Unit
) : ListAdapter<WaterIntake, WaterIntakeAdapter.WaterIntakeViewHolder>(WaterIntakeDiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterIntakeViewHolder {
        val binding = ItemWaterIntakeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WaterIntakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaterIntakeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WaterIntakeViewHolder(
        private val binding: ItemWaterIntakeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(intake: WaterIntake) {
            binding.tvAmount.text = String.format("%.2f L", intake.amount)
            binding.tvTime.text = timeFormat.format(Date(intake.timestamp))

            binding.btnDelete.setOnClickListener {
                onDeleteClick(intake)
            }
        }
    }

    private class WaterIntakeDiffCallback : DiffUtil.ItemCallback<WaterIntake>() {
        override fun areItemsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem == newItem
        }
    }
}