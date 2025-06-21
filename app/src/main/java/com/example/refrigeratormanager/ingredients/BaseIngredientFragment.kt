package com.example.refrigeratormanager.ingredients

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.refrigeratormanager.product.Product
import com.example.refrigeratormanager.product.ProductManager
import com.example.refrigeratormanager.product.ProductRepository
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.product.ProductAdapter

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
        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(storageIndex) ?: emptyList()

        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.productRecyclerView)
        val noProductsText = binding.root.findViewById<TextView>(R.id.textNoProducts)

        if (products.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            noProductsText.visibility = View.GONE

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.adapter = ProductAdapter(products)
        } else {
            recyclerView.visibility = View.GONE
            noProductsText.visibility = View.VISIBLE
        }
    }

}
