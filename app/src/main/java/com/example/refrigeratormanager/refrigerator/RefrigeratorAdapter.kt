package com.example.refrigeratormanager.refrigerator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.databinding.ItemRefrigeratorBinding

class RefrigeratorAdapter(
    private val onItemClick: (Refrigerator) -> Unit
) : ListAdapter<Refrigerator, RefrigeratorAdapter.ViewHolder>(RefrigeratorDiffCallback) {

    inner class ViewHolder(private val binding: ItemRefrigeratorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(refrigerator: Refrigerator) {
            binding.tvRefrigeratorName.text = refrigerator.name
            binding.root.setOnClickListener { onItemClick(refrigerator) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRefrigeratorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val RefrigeratorDiffCallback = object : DiffUtil.ItemCallback<Refrigerator>() {
            override fun areItemsTheSame(oldItem: Refrigerator, newItem: Refrigerator): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Refrigerator, newItem: Refrigerator): Boolean {
                return oldItem == newItem
            }
        }
    }
}
