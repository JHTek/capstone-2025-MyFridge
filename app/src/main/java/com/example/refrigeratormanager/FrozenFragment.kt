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

class FrozenFragment : Fragment() {
    private lateinit var binding: FragmentFrozenBinding
    private var refrigeratorName: String? = null
    // 냉장고 고유 ID와 이름을 저장하는 변수
    private var refrigeratorId: Int = -1 // 전달받는 냉장고 ID

    companion object {
        // Fragment를 생성할 때 냉장고 ID와 이름을 넘길 수 있도록 팩토리 메서드 제공
        fun newInstance(refrigeratorId: Int, refrigeratorName: String): FrozenFragment {
            val fragment = FrozenFragment()
            val args = Bundle()
            args.putInt("refrigerator_id", refrigeratorId)
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }

    // JWT 토큰을 SharedPreferences에서 가져오기
    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrozenBinding.inflate(inflater, container, false)
        // 인자로 전달받은 냉장고 ID 및 이름을 가져옴
        refrigeratorName = arguments?.getString("refrigerator_name") ?: ""
        refrigeratorId = arguments?.getInt("refrigerator_id") ?: -1

        // 서버에서 데이터를 가져와서 UI에 표시
        if (refrigeratorId != -1) {
            loadAndDisplayProducts() // 서버에서 로드 후 displayProducts 호출
        }

        return binding.root
    }

    // 서버에서 식재료 데이터를 가져와 ProductManager에 저장하고 화면에 표시
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

        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(1) //냉장(0), 냉동(1), 실온(2)
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
                    // 삭제 버튼 클릭 시 로컬 목록에서 삭제 후 화면 갱신
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
