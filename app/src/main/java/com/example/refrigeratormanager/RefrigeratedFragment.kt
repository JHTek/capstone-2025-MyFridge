package com.example.refrigeratormanager


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentRefrigeratedBinding

class RefrigeratedFragment : Fragment() {
    private lateinit var binding: FragmentRefrigeratedBinding
    private var refrigeratorName: String? = null

    companion object {
        fun newInstance(refrigeratorName: String): RefrigeratedFragment {
            val fragment = RefrigeratedFragment()
            val args = Bundle()
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRefrigeratedBinding.inflate(inflater, container, false)
        refrigeratorName = arguments?.getString("refrigerator_name")
        displayProducts()
        return binding.root
    }

    private fun displayProducts() {
        binding.productContainer.removeAllViews()

        val products = ProductManager.getSortedProductsByExpiration(refrigeratorName)?.get(0) // 냉장(0)
        if (products != null && products.isNotEmpty()) {
            products.forEach { product ->
                val view =
                    layoutInflater.inflate(R.layout.item_product, binding.productContainer, false)
                val productName = view.findViewById<TextView>(R.id.productNameTextView)
                val productQuantity = view.findViewById<TextView>(R.id.quantityTextView)
                val productExpiration = view.findViewById<TextView>(R.id.expirationDateTextView)
                val deleteButton = view.findViewById<Button>(R.id.deleteButton)

                productName.text = product.ingredientsName
                productQuantity.text = "수량: ${product.quantity}"
                productExpiration.text = "유통기한: ${product.expirationDate}"

                deleteButton.setOnClickListener {
                    ProductManager.removeProduct(refrigeratorName!!, product)
                    displayProducts()  // 삭제 후 새로 고침
                }

                binding.productContainer.addView(view)
            }
        } else {
            val noProductsText = TextView(requireContext()).apply {
                text = "제품이 없습니다."
                gravity = Gravity.CENTER
                textSize = 16f
            }
            binding.productContainer.addView(noProductsText)
        }
    }
}
