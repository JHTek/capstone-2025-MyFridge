package com.example.refrigeratormanager.ingredients

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.refrigeratormanager.Product
import com.example.refrigeratormanager.ProductManager
import com.example.refrigeratormanager.ProductRepository
import com.example.refrigeratormanager.R

abstract class BaseIngredientFragment<VB : ViewBinding> : Fragment() {
    protected lateinit var binding: VB
    protected var refrigeratorName: String? = null
    protected var refrigeratorId: Int = -1

    abstract val storageIndex: Int
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = bindingInflater(inflater, container, false)
        refrigeratorName = arguments?.getString("refrigerator_name") ?: ""
        refrigeratorId = arguments?.getInt("refrigerator_id") ?: -1

        if (refrigeratorId != -1) {
            loadAndDisplayProducts()
        }
        return binding.root
    }

    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    private fun loadAndDisplayProducts() {
        val token = getToken() ?: return

        ProductRepository.fetchProducts(
            token = token,
            refrigeratorId = refrigeratorId,
            onSuccess = { ingredients ->
                val products = ingredients.map {
                    Product(
                        ingredientsName = it.ingredientsName,
                        quantity = it.quantity,
                        expirationDate = it.expirationDate,
                        storageLocation = it.storageLocation,
                        refrigeratorId = refrigeratorId,
                        category = it.category
                    )
                }
                ProductManager.updateProducts(refrigeratorId, products)
                displayProducts()
            },
            onFailure = {
                Toast.makeText(requireContext(), "식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun displayProducts() {
        Log.d("DEBUG", "✅ displayProducts() 호출됨")
        val container = (binding.root.findViewById<ViewGroup>(R.id.productContainer))
        container.removeAllViews()

        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(storageIndex)
        Log.d("DEBUG", "✅ 받아온 제품 수: ${products?.size ?: 0}")
        if (!products.isNullOrEmpty()) {
            products.forEach { product ->
                val view = layoutInflater.inflate(R.layout.item_product_with_image, container, false)

                Log.d("DEBUG", "➡️ 제품 추가: ${product.ingredientsName}")

                view.findViewById<ImageView>(R.id.categoryImageView).setImageResource(ImageUtils.getImageResourceForCategory(product.category))
                view.findViewById<TextView>(R.id.productNameTextView).text = product.ingredientsName
//                view.findViewById<TextView>(R.id.quantityTextView).text = "수량: ${product.quantity}"
//                view.findViewById<TextView>(R.id.expirationDateTextView).text = "유통기한: ${product.expirationDate}"
//                view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
//                    ProductManager.removeProduct(refrigeratorId, product)
//                    displayProducts()
//                }
                container.addView(view)
            }
        } else {
            val noProductsText = TextView(requireContext()).apply {
                text = "제품이 없습니다."
                gravity = Gravity.CENTER
                textSize = 16f
            }
            container.addView(noProductsText)
        }
    }
}
