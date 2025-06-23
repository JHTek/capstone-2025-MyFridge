package com.example.refrigeratormanager.product

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.ingredients.ImageUtils

class ProductAdapter(
    private val productList: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.categoryImageView)
        val name: TextView = itemView.findViewById(R.id.productNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_with_image, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.image.setImageResource(ImageUtils.getImageResourceForCategory(product.category))
        holder.name.text = product.ingredientsName

        // 위치에 따라 정렬 방향 설정
        val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        val columnIndex = position % 3

        when (columnIndex) {
            0 -> holder.itemView.gravityLeft()
            1 -> holder.itemView.gravityCenter()
            2 -> holder.itemView.gravityRight()
        }

        holder.itemView.setOnClickListener {
            onClick(product)  // ← 클릭 시 전달된 product를 호출자에게 전달
        }
    }


    override fun getItemCount(): Int = productList.size
}

fun View.gravityLeft() {
    (this.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.START
}

fun View.gravityCenter() {
    (this.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
}

fun View.gravityRight() {
    (this.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.END
}
