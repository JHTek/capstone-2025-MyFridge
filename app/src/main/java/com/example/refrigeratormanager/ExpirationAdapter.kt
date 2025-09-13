package com.example.refrigeratormanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.databinding.ItemExpirationBinding
import com.example.refrigeratormanager.product.Product

class ExpirationAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ExpirationAdapter.ExpirationViewHolder>() {

    inner class ExpirationViewHolder(val binding: ItemExpirationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpirationViewHolder {
        val binding = ItemExpirationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpirationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpirationViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.ingredientsName
        holder.binding.tvRefrigerator.text = "냉장고: ${product.refrigeratorName}"
        holder.binding.tvExpirationDate.text = "유통기한: ${product.expirationDate}"
        holder.binding.tvQuantity.text = "수량: ${product.quantity}"
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}
