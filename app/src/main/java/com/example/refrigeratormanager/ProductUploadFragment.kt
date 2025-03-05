package com.example.refrigeratormanager

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.refrigeratormanager.DTO.ApiResponse
import com.example.refrigeratormanager.IngredientRequestDTO
import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.databinding.FragmentProductUploadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import org.threeten.bp.LocalDate

class ProductUploadFragment : DialogFragment() {

    private var _binding: FragmentProductUploadBinding? = null
    private val binding get() = _binding!!

    private var userRefrigerators: List<String> = listOf() // 유저의 냉장고 리스트
    private val storageTypes = listOf("냉장", "냉동", "실온") // 보관 방식 리스트

    private val productFields = mutableListOf<LinearLayout>() // 동적으로 생성되는 상품 입력 필드를 저장할 리스트

    private val refrigeratorViewModel: RefrigeratorViewModel by viewModels() //RefrigeratorViewModel


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentProductUploadBinding.inflate(LayoutInflater.from(context))

        setupUI()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupUI() {
        val productMap = arguments?.getSerializable("productData") as? Map<String, Int> ?: emptyMap()
        val productName = arguments?.getString("productName") ?: "" //상품명 받아오기
        // ✅ Log로 받은 데이터 확인
        Log.d("ProductUploadFragment", "받은 데이터: $productMap")

        // ✅ 기존 등록 필드 초기화
        binding.productFieldsContainer.removeAllViews()
        productFields.clear()

        val productList = productMap.entries.toList()

        if (productList.isNotEmpty()) {
            // ✅ 첫 번째 상품은 기존 필드를 사용
            val firstEntry = productList.first()
            binding.productNameEditText.setText(firstEntry.key)
            binding.quantityEditText.setText(firstEntry.value.toString())

            // ✅ 두 번째 상품부터 동적으로 추가
            productList.drop(1).forEach { (name, quantity) ->
                addProductField(true, name, quantity)
            }
        } else {
            // ✅ 데이터가 없을 경우 기본 필드 추가
            binding.productNameEditText.setText(productName)
            binding.quantityEditText.setText("1") // 기본 수량 설정
        }
        loadUserRefrigerators()
        setupStorageTypeSpinner()
        setupEventListeners()
        // 기본 상품 필드 추가
        addProductField(false)
    }

    // 유저의 냉장고 목록을 로드하여 Spinner 설정
    private fun loadUserRefrigerators() {
        val auth = getToken()
        val token = "Bearer $auth"

        // CoroutineScope로 비동기 호출
        val apiClient = ApiClient.getRefrigeratorApi()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiClient.getRefrigerators(token)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val refrigerators = response.body()
                        if (refrigerators != null && refrigerators.isNotEmpty()) {
                            userRefrigerators = refrigerators.map { it.name }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                userRefrigerators
                            )
                            binding.refrigeratorSpinner.adapter = adapter
                            binding.uploadButton.isEnabled = true
                        } else {
                            binding.refrigeratorSpinner.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                listOf("없음")
                            )
                            binding.uploadButton.isEnabled = false
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load refrigerators", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 저장 타입 (냉장, 냉동, 실온) Spinner 설정 리뷰
    private fun setupStorageTypeSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, storageTypes)
        binding.storageTypeSpinner.adapter = adapter
    }

    // 유저 냉장고 데이터를 가져오는 함수 (실제 데이터베이스 연동 필요)
    private fun getUserRefrigeratorsFromDatabase(): List<String> {
        return listOf() // 실제 구현 필요
    }

    // 유통기한 선택 다이얼로그 표시
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editText.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }
    //리뷰
    private var selectedRefrigeratorId: Int? = null

    private fun setupEventListeners() {
        binding.refrigeratorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRefrigeratorName = parent?.getItemAtPosition(position).toString()
                // userRefrigerators에서 선택된 냉장고 이름을 찾아 ID를 설정
                val selectedRefrigerator = userRefrigerators.getOrNull(position)
                selectedRefrigeratorId = selectedRefrigerator?.let { name ->
                    // 냉장고 이름을 기반으로 ID를 찾는 로직 (예: 서버에서 반환된 데이터 구조에 따라 다름)
                    // 여기서는 임시로 position을 ID로 사용
                    position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRefrigeratorId = null
            }
        }

        binding.calendarButton.setOnClickListener { showDatePickerDialog(binding.expirationDateEditText) }

        binding.uploadButton.setOnClickListener {
            uploadProduct()
        }

        binding.btnCancel.setOnClickListener { dismiss() } // 취소 버튼 누르면 팝업 닫기

        binding.addProductButton.setOnClickListener { addProductField(true) } // 추가 버튼 클릭 시 새로운 상품 등록 필드 추가
    }

    private fun addProductField(isVisible: Boolean, productName: String = "", quantity: Int = 1) {
        val inflater = LayoutInflater.from(requireContext())
        val newProductField = inflater.inflate(R.layout.layout_product_input, binding.productFieldsContainer, false)

        // 동적으로 추가된 필드의 각 뷰들 초기화
        val refrigeratorSpinner = newProductField.findViewById<Spinner>(R.id.refrigeratorSpinner)
        val storageTypeSpinner = newProductField.findViewById<Spinner>(R.id.storageTypeSpinner)
        val expirationDateEditText = newProductField.findViewById<EditText>(R.id.expirationDateEditText)
        val calendarButton = newProductField.findViewById<ImageButton>(R.id.calendarButton)
        val productNameEditText = newProductField.findViewById<EditText>(R.id.productNameEditText)
        val quantityEditText = newProductField.findViewById<EditText>(R.id.quantityEditText)

        // 냉장고 선택 (동적 스피너 설정)
        val refrigeratorAdapter: ArrayAdapter<String>
        if (userRefrigerators.isEmpty()) {
            refrigeratorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("없음"))
        } else {
            refrigeratorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userRefrigerators)
        }
        refrigeratorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        refrigeratorSpinner.adapter = refrigeratorAdapter

        // 저장 타입 선택 (동적 스피너 설정)
        val storageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, storageTypes)
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        storageTypeSpinner.adapter = storageAdapter

        // ✅ 상품 정보 자동 입력 (기본값: 빈 값, 수량 1)
        productNameEditText.setText(productName)
        quantityEditText.setText(quantity.toString())

        // 유통기한 설정 (기본으로 비워두고, 필요시 선택할 수 있음)
        expirationDateEditText.setText("")
        calendarButton.setOnClickListener { showDatePickerDialog(expirationDateEditText) }

        // ✅ 필드가 표시되어야 할 경우에만 추가
        if (isVisible) {
            binding.productFieldsContainer.addView(newProductField)
            productFields.add(newProductField as LinearLayout) // 동적 필드 리스트에 추가
        }
    }
    // 제품 업로드 처리
    private fun uploadProduct() {
        val selectedRefrigeratorId = selectedRefrigeratorId
        if (selectedRefrigeratorId == null) {
            Toast.makeText(requireContext(), "냉장고를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 각 추가된 상품 필드에 대해 정보를 가져옴
        for (productField in productFields) {
            val productNameInput = productField.findViewById<EditText>(R.id.productNameEditText)
            val quantityInput = productField.findViewById<EditText>(R.id.quantityEditText)
            val expirationDateInput = productField.findViewById<EditText>(R.id.expirationDateEditText)
            val storageLocationSpinner = productField.findViewById<Spinner>(R.id.storageTypeSpinner)

            val ingredientsName = productNameInput.text.toString().trim()
            val quantity = quantityInput.text.toString().trim().toIntOrNull()
            val expirationDate = expirationDateInput.text.toString().trim()
            val storageLocation = storageLocationSpinner.selectedItemPosition // 저장 위치 (0: 냉장, 1: 냉동, 2: 실온)

            if (ingredientsName.isEmpty() || quantity == null || expirationDate.isEmpty()) {
                Toast.makeText(requireContext(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show()
                return
            }

            // expirationDate를 LocalDate로 변환
            val localDate = LocalDate.parse(expirationDate) // "yyyy-MM-dd" 형식이어야 함

            // IngredientRequestDTO 객체 생성
            val ingredientRequest = IngredientRequestDTO(
                refrigeratorId = selectedRefrigeratorId,
                ingredientsName = ingredientsName,
                quantity = quantity,
                expirationDate = localDate,
                storageLocation = storageLocation
            )

            // 서버로 전송
            sendIngredientToServer(ingredientRequest)
        }

        Toast.makeText(requireContext(), "상품이 업로드되었습니다!", Toast.LENGTH_SHORT).show()
        dismiss() // 업로드 후 다이얼로그 닫기
    }

    //토큰 가져오기
    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }
    private fun sendIngredientToServer(ingredientRequest: IngredientRequestDTO) {
        val token = getToken() ?: run {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val call = ApiClient.getIngredientApi().uploadIngredient("Bearer $token", ingredientRequest)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "재료가 업로드되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "재료 업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}