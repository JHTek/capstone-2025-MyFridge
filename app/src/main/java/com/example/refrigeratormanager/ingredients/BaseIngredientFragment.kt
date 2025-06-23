package com.example.refrigeratormanager.ingredients

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.activity.OnBackPressedCallback


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
                        ingredientsId = it.ingredientsId,
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
        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.productRecyclerView)
        val textNoProducts = binding.root.findViewById<TextView>(R.id.textNoProducts)
        val childContainer = binding.root.findViewById<FrameLayout>(R.id.childFragmentContainer) // ✅

        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(storageIndex)

        if (!products.isNullOrEmpty()) {
            recyclerView.visibility = View.VISIBLE
            textNoProducts.visibility = View.GONE
            childContainer.visibility = View.GONE // 리스트 표시 중에는 숨김

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.adapter = ProductAdapter(products) { product ->
                // ✅ 상세 보기 클릭 시 기존 목록 숨기고 상세 프래그먼트 표시
                recyclerView.visibility = View.GONE
                textNoProducts.visibility = View.GONE
                childContainer.visibility = View.VISIBLE

                val detailFragment = IngredientDetailFragment.newInstance(product)
                childFragmentManager.beginTransaction()
                    .replace(R.id.childFragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            recyclerView.visibility = View.GONE
            textNoProducts.visibility = View.VISIBLE
            childContainer.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 삭제 결과 수신 처리
        parentFragmentManager.setFragmentResultListener("delete_result", viewLifecycleOwner) { _, bundle ->
            val wasDeleted = bundle.getBoolean("deleted")
            if (wasDeleted) {
                loadAndDisplayProducts()  // 🔁 삭제 후 목록 새로고침
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val childManager = childFragmentManager
                    if (childManager.backStackEntryCount > 0) {
                        childManager.popBackStack()

                        // ✅ 프래그먼트 UI 상태 복구
                        binding.root.findViewById<FrameLayout>(R.id.childFragmentContainer).visibility = View.GONE
                        binding.root.findViewById<RecyclerView>(R.id.productRecyclerView).visibility = View.VISIBLE

                        val products = ProductManager.getSortedProductsByExpiration(refrigeratorId).getOrNull(storageIndex)
                        binding.root.findViewById<TextView>(R.id.textNoProducts).visibility =
                            if (products.isNullOrEmpty()) View.VISIBLE else View.GONE
                    } else {
                        // 기본 뒤로가기 동작 실행
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }
}
