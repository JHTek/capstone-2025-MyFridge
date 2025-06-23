import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentEditIngredientBinding
import com.example.refrigeratormanager.ingredients.ImageUtils
import com.example.refrigeratormanager.product.Product
import com.example.refrigeratormanager.product.ProductRepository
import java.text.SimpleDateFormat
import java.util.*

class EditIngredientFragment : Fragment() {
    private lateinit var binding: FragmentEditIngredientBinding
    private lateinit var product: Product

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): EditIngredientFragment {
            val fragment = EditIngredientFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as Product
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기존 정보 표시
        binding.imageCategory.setImageResource(ImageUtils.getImageResourceForCategory(product.category))
        binding.etName.setText(product.ingredientsName)
        binding.etQuantity.setText(product.quantity.toString())
        binding.etCategory.setText(product.category)
        binding.etExpirationDate.setText(product.expirationDate)

        // 날짜 선택기 연결
        binding.etExpirationDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 저장 버튼 클릭
        binding.btnSave.setOnClickListener {
            val updatedProduct = product.copy(
                ingredientsName = binding.etName.text.toString(),
                quantity = binding.etQuantity.text.toString().toIntOrNull() ?: product.quantity,
                category = binding.etCategory.text.toString(),
                expirationDate = binding.etExpirationDate.text.toString()
            )

            val token = getToken() ?: return@setOnClickListener
            ProductRepository.updateProduct(
                token = token,
                updatedProduct = updatedProduct,
                onSuccess = {
                    Toast.makeText(requireContext(), "수정 완료", Toast.LENGTH_SHORT).show()

                    // ✅ 수정된 데이터 전달
                    val result = Bundle().apply {
                        putSerializable("updated_product", updatedProduct)
                    }
                    parentFragmentManager.setFragmentResult("edit_result", result)

                    // ✅ 이전 화면으로 돌아가기
                    parentFragmentManager.popBackStack()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "수정 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            binding.etExpirationDate.setText(sdf.format(selectedDate.time))
        }

        DatePickerDialog(
            requireContext(), listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getToken(): String? {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("JWT_TOKEN", null)
    }
}
