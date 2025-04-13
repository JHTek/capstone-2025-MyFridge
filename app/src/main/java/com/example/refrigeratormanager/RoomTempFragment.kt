package com.example.refrigeratormanager

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentFrozenBinding
import com.example.refrigeratormanager.databinding.FragmentRoomTempBinding

class RoomTempFragment : Fragment() {
    private lateinit var binding: FragmentRoomTempBinding
    private var refrigeratorName: String? = null
    private var refrigeratorId: Int = -1 // 전달받는 냉장고 ID

    companion object {
        fun newInstance(refrigeratorId: Int, refrigeratorName: String): RoomTempFragment {
            val fragment = RoomTempFragment()
            val args = Bundle()
            args.putInt("refrigerator_id", refrigeratorId)
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomTempBinding.inflate(inflater, container, false)
        refrigeratorName = arguments?.getString("refrigerator_name") ?: ""
        refrigeratorId = arguments?.getInt("refrigerator_id") ?: -1

        if (refrigeratorId != -1) {
            loadAndDisplayProducts() // 서버에서 로드 후 displayProducts 호출
        }

        return binding.root
    }

    private fun loadAndDisplayProducts() {
        val token = getToken() ?: return

        ProductRepository.fetchProducts(
            token = token,
            refrigeratorId = refrigeratorId,
            onSuccess = { ingredients ->
                // 서버 응답 DTO를 로컬 Product 모델로 변환
                val products = ingredients.map {
                    Product(
                        ingredientsName = it.ingredientsName,
                        quantity = it.quantity,
                        expirationDate = it.expirationDate,
                        storageLocation = it.storageLocation,
                        refrigeratorId = refrigeratorId
                    )
                }

                // 저장
                ProductManager.updateProducts(refrigeratorId, products)

                // 화면 표시
                displayProducts()
            },
            onFailure = {
                Toast.makeText(requireContext(), "식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun displayProducts() {
        binding.productContainer.removeAllViews()

        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(2) //냉장(0), 냉동(1), 실온(2)
        if (!products.isNullOrEmpty()) {
            products.forEach { product ->
                val view = layoutInflater.inflate(R.layout.item_product, binding.productContainer, false)
                val productName = view.findViewById<TextView>(R.id.productNameTextView)
                val productQuantity = view.findViewById<TextView>(R.id.quantityTextView)
                val productExpiration = view.findViewById<TextView>(R.id.expirationDateTextView)
                val deleteButton = view.findViewById<Button>(R.id.deleteButton)

                productName.text = product.ingredientsName
                productQuantity.text = "수량: ${product.quantity}"
                productExpiration.text = "유통기한: ${product.expirationDate}"

                deleteButton.setOnClickListener {
                    ProductManager.removeProduct(refrigeratorId, product)
                    displayProducts()
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
