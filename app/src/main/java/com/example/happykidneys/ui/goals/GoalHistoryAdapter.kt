package com.example.happykidneys.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.happykidneys.R
import com.example.happykidneys.data.database.entities.Goal
import com.example.happykidneys.databinding.ItemGoalHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class GoalHistoryAdapter : ListAdapter<Goal, GoalHistoryAdapter.GoalViewHolder>(GoalDiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoalViewHolder(
        private val binding: ItemGoalHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal) {
            try {
                val date = dateFormat.parse(goal.date)
                binding.tvDate.text = displayDateFormat.format(date ?: Date())
            } catch (e: Exception) {
                binding.tvDate.text = goal.date
            }

            binding.tvGoalAmount.text = String.format("Goal: %.1f L", goal.targetAmount)
            binding.tvActualAmount.text = String.format("Actual: %.1f L", goal.actualAmount)

            val percentage = ((goal.actualAmount / goal.targetAmount) * 100).toInt()
            binding.tvPercentage.text = "$percentage%"

            if (goal.achieved) {
                binding.tvStatus.text = binding.root.context.getString(R.string.achieved)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.success)
                )
                binding.ivStatus.setImageResource(R.drawable.ic_check_circle)
                binding.ivStatus.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.success)
                )
            } else {
                binding.tvStatus.text = binding.root.context.getString(R.string.not_achieved)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
                binding.ivStatus.setImageResource(R.drawable.ic_cancel)
                binding.ivStatus.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
            }

            binding.progressBar.progress = percentage.coerceAtMost(100)
        }
    }

    private class GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem == newItem
        }
    }
}