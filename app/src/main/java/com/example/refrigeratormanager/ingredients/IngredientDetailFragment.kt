package com.example.refrigeratormanager.ingredients

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.databinding.FragmentIngredientDetailBinding
import com.example.refrigeratormanager.product.Product
import com.example.refrigeratormanager.product.ProductRepository


class IngredientDetailFragment : Fragment() {
    private lateinit var binding: FragmentIngredientDetailBinding
    private lateinit var product: Product

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): IngredientDetailFragment {
            val fragment = IngredientDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("IngredientDetail", "onCreate 시작됨")
        try {
            val arg = arguments?.getSerializable(ARG_PRODUCT)
            Log.d("IngredientDetail", "인자로 전달된 값: $arg")
            product = arg as? Product ?: throw IllegalArgumentException("Product가 null입니다")
        } catch (e: Exception) {
            Log.e("IngredientDetail", "onCreate에서 오류 발생", e)
            Toast.makeText(requireContext(), "재료 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngredientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = getToken() ?: return
        ProductRepository.getProductByIdViaRefrigerator(
            token = token,
            refrigeratorId = product.refrigeratorId,
            productId = product.ingredientsId,
            onSuccess = { updatedProduct ->
                updateUIWithProduct(updatedProduct)
            },
            onFailure = {
                Toast.makeText(requireContext(), "최신 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        )

        // 기본 정보 표시
        binding.imageCategory.setImageResource(ImageUtils.getImageResourceForCategory(product.category))
        binding.tvName.text = product.ingredientsName
        binding.tvQuantity.text = "수량: ${product.quantity}"
        binding.tvExpirationDate.text = "유통기한: ${product.expirationDate}"
        binding.tvCategory.text = "카테고리: ${product.category}"

        // 메모 표시
        binding.etMemo.setText(product.note ?: "")

        parentFragmentManager.setFragmentResultListener("edit_result", viewLifecycleOwner) { _, bundle ->
            val updatedProduct = bundle.getSerializable("updated_product") as? Product
            updatedProduct?.let {
                updateUIWithProduct(it)
            }
        }

        // 메모 저장 버튼
        binding.btnSaveMemo.setOnClickListener {
            val updatedMemo = binding.etMemo.text.toString()
            val token = getToken() ?: return@setOnClickListener

            ProductRepository.updateProductNote(
                token = token,
                productId = product.ingredientsId,
                note = updatedMemo,
                onSuccess = {
                    product = product.copy(note = updatedMemo)  // ✅ product 내부 갱신
                    Toast.makeText(requireContext(), "메모 저장 완료", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "메모 저장 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 수정 버튼
        binding.btnEdit.setOnClickListener {
            val container = requireActivity().findViewById<FrameLayout>(R.id.childFragmentContainer)
            container.visibility = View.VISIBLE

            val editFragment = EditIngredientFragment.newInstance(product)

            // ✅ 반드시 childFragmentManager를 사용하고, ID는 childFragmentContainer로
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.childFragmentContainer, editFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        // 삭제 버튼
        binding.btnDelete.setOnClickListener {
            val token = getToken() ?: return@setOnClickListener
            ProductRepository.deleteProduct(
                token = token,
                productId = product.ingredientsId,
                onSuccess = {
                    // ✅ 부모의 부모에 이벤트 전달
                    parentFragment?.parentFragmentManager?.setFragmentResult("delete_result", Bundle().apply {
                        putBoolean("deleted", true)
                        putInt("deleted_id", product.ingredientsId)
                    })

                    Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }


    }

    private fun getToken(): String? {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("JWT_TOKEN", null)
    }

    private fun updateUIWithProduct(product: Product) {
        this.product = product  // 내부 변수도 갱신

        binding.imageCategory.setImageResource(ImageUtils.getImageResourceForCategory(product.category))
        binding.tvName.text = product.ingredientsName
        binding.tvQuantity.text = "수량: ${product.quantity}"
        binding.tvExpirationDate.text = "유통기한: ${product.expirationDate}"
        binding.tvCategory.text = "카테고리: ${product.category}"
        binding.etMemo.setText(product.note ?: "")
    }

}

