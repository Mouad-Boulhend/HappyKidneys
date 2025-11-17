package com.example.happykidneys.ui.tips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.happykidneys.databinding.ItemTipBinding

class TipsAdapter(
    private val onShareClick: (HydrationTip) -> Unit
) : ListAdapter<HydrationTip, TipsAdapter.TipViewHolder>(TipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val binding = ItemTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TipViewHolder(
        private val binding: ItemTipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: HydrationTip) {
            binding.tvTipTitle.text = tip.title
            binding.tvTipDescription.text = tip.description
            binding.ivTipIcon.setImageResource(tip.icon)

            binding.btnShare.setOnClickListener {
                onShareClick(tip)
            }
        }
    }

    private class TipDiffCallback : DiffUtil.ItemCallback<HydrationTip>() {
        override fun areItemsTheSame(oldItem: HydrationTip, newItem: HydrationTip): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: HydrationTip, newItem: HydrationTip): Boolean {
            return oldItem == newItem
        }
    }
}