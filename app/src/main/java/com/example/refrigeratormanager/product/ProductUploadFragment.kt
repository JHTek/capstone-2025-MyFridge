package com.example.refrigeratormanager.product

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.DTO.ApiResponse
import com.example.refrigeratormanager.HomeActivity
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.refrigerator.Refrigerator
import com.example.refrigeratormanager.refrigerator.RefrigeratorViewModel
import com.example.refrigeratormanager.ingredients.IngredientRequestDTO
import com.example.refrigeratormanager.databinding.FragmentProductUploadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProductUploadFragment : DialogFragment() {

    private var _binding: FragmentProductUploadBinding? = null
    private val binding get() = _binding!!

    private var userRefrigerators: List<String> = listOf() // 유저의 냉장고 리스트
    private var refrigerators: List<Refrigerator> = listOf()
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
        val quantity = arguments?.getString("quantity") ?: "1" // 기본 수량 설정
        Log.d("ProductUploadFragment", "받은 이름: $productName, 수량: $quantity")




        binding.expirationDateEditText.setText("") // 유통기한 비워둠
        binding.storageTypeSpinner.setSelection(0) // 기본값으로 "냉장" 설정

        val productList = productMap.entries.toList()

        if (productList.isNotEmpty()) {
            // ✅ 첫 번째 상품은 기존 필드를 사용
            val firstEntry = productList.first()
            binding.productNameEditText.setText(firstEntry.key)
            binding.quantityEditText.setText(firstEntry.value.toString())
        } else {
            // ✅ 데이터가 없을 경우 기본 필드 추가
            binding.productNameEditText.setText(productName)
            binding.quantityEditText.setText(quantity)
        }

        loadUserRefrigerators()
        setupStorageTypeSpinner()
        setupEventListeners()

        // 기본 상품 필드 추가 (이후 값이 업로드될 수 있도록 productFields에 추가)
        addProductField(false)

        // 기본 필드도 productFields에 추가
        val firstProductField = binding.productFieldsContainer.getChildAt(0)
        if (firstProductField is LinearLayout) {
            productFields.add(firstProductField)
        } else {
            Log.e("ProductUpload", "First product field is not a LinearLayout")
        }
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
                        val refrigeratorsResponse = response.body()
                        if (refrigeratorsResponse != null && refrigeratorsResponse.isNotEmpty()) {
                            // 수정: refrigerators 변수에 냉장고 객체 리스트 저장
                            refrigerators = refrigeratorsResponse
                            userRefrigerators = refrigerators.map { it.name } // 이름만 추출

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                userRefrigerators
                            )
                            binding.refrigeratorSpinner.adapter = adapter

                            // ✅ [추가] 냉장고 목록이 준비된 이후에 동적 필드 추가
                            addDynamicProductFieldsIfNeeded()

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

    // ✅ [추가 함수] 냉장고 데이터가 로드된 후에만 동적 상품 필드를 추가
    private fun addDynamicProductFieldsIfNeeded() {
        val productMap = arguments?.getSerializable("productData") as? Map<String, Int> ?: return

        // 첫 번째 항목은 이미 setupUI()에서 처리됨, 나머지만 추가
        val productList = productMap.entries.toList().drop(1)
        productList.forEach { (name, quantity) ->
            addProductField(true, name, quantity)  // 이 시점엔 userRefrigerators가 채워져 있음
        }
    }

    // 날짜 선택 다이얼로그
    private fun showDatePickerDialog(expirationDateInput: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                expirationDateInput.setText(formattedDate)
            },
            year,
            month,
            day
        )
        // 현재 날짜 이전 선택 불가능하게 설정
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }
    //리뷰
    private var selectedRefrigeratorId: Int? = null

    private fun setupEventListeners() {
        binding.refrigeratorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRefrigerator = refrigerators.getOrNull(position)
                selectedRefrigeratorId = selectedRefrigerator?.id
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
    private val refrigeratorIdMap = mutableMapOf<View, Int?>() //동적필드용 냉장고 매핑
    // 제품 업로드 처리
    private fun uploadProduct() {
        val selectedRefrigeratorId = selectedRefrigeratorId
        if (selectedRefrigeratorId == null) {
            Toast.makeText(requireContext(), "냉장고를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 모든 필드의 데이터를 리스트로 수집
        val ingredientRequests = mutableListOf<IngredientRequestDTO>()

        // 기본 필드 데이터 수집
        val baseProductName = binding.productNameEditText.text.toString().trim()
        val baseQuantity = binding.quantityEditText.text.toString().trim().toIntOrNull()
        val baseExpirationDate = binding.expirationDateEditText.text.toString().trim()
        val baseStorageLocation = binding.storageTypeSpinner.selectedItemPosition

        // 기본 필드 유효성 검사
        if (baseProductName.isEmpty() || baseQuantity == null || baseExpirationDate.isEmpty()) {
            Toast.makeText(requireContext(), "기본 필드의 모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 기본 필드 데이터를 리스트에 추가
        ingredientRequests.add(
            IngredientRequestDTO(
                refrigeratorId = selectedRefrigeratorId,
                ingredientsName = baseProductName,
                quantity = baseQuantity,
                expirationDate = baseExpirationDate,
                storageLocation = baseStorageLocation

            )
        )

        // 동적 필드 데이터 수집
        for (i in 1 until productFields.size) {
            val productField = productFields[i]
            val productNameInput = productField.findViewById<EditText>(R.id.productNameEditText)
            val quantityInput = productField.findViewById<EditText>(R.id.quantityEditText)
            val expirationDateInput = productField.findViewById<EditText>(R.id.expirationDateEditText)
            val storageLocationSpinner = productField.findViewById<Spinner>(R.id.storageTypeSpinner)




            val ingredientsName = productNameInput.text.toString().trim()
            val quantity = quantityInput.text.toString().trim().toIntOrNull()
            val expirationDate = expirationDateInput.text.toString().trim()
            val storageLocation = storageLocationSpinner.selectedItemPosition
            val selectedRefrigeratorId = refrigeratorIdMap[productField]

            // 유효성 검사
            if (ingredientsName.isEmpty() || quantity == null || expirationDate.isEmpty() || selectedRefrigeratorId == null) {
                Toast.makeText(requireContext(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show()
                return
            }

            // 동적 필드 데이터를 리스트에 추가
            ingredientRequests.add(
                IngredientRequestDTO(
                    refrigeratorId = selectedRefrigeratorId,
                    ingredientsName = ingredientsName,
                    quantity = quantity,
                    expirationDate = expirationDate ,
                    storageLocation = storageLocation
                )
            )
        }

        // 서버로 전송
        sendIngredientsToServer(ingredientRequests)
    }

    // 동적으로 추가된 상품 필드 값 가져오기
    private fun addProductField(isVisible: Boolean, productName: String = "", quantity: Int = 1) {
        val inflater = LayoutInflater.from(requireContext())
        val newProductField = inflater.inflate(R.layout.layout_product_input, binding.productFieldsContainer, false)

        // 동적으로 추가된 필드의 각 뷰들 초기화
        val refrigeratorSpinner = newProductField.findViewById<Spinner>(R.id.refrigeratorSpinner)
        val storageTypeSpinner = newProductField.findViewById<Spinner>(R.id.storageTypeSpinner)
        val expirationDateEditText = newProductField.findViewById<EditText>(R.id.expirationDateEditText)
        val productNameEditText = newProductField.findViewById<EditText>(R.id.productNameEditText)
        val quantityEditText = newProductField.findViewById<EditText>(R.id.quantityEditText)

        // 냉장고 선택 (동적 스피너 설정)
        val refrigeratorAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userRefrigerators)
        refrigeratorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        refrigeratorSpinner.adapter = refrigeratorAdapter

        // 냉장고 Spinner에 리스너 추가
        var selectedRefrigeratorId: Int? = null // 동적 필드의 냉장고 ID 저장
        refrigeratorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRefrigeratorId = refrigerators.getOrNull(position)?.id // 선택된 냉장고 ID 저장
                refrigeratorIdMap[newProductField] = selectedRefrigeratorId // 맵에 저장
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRefrigeratorId = null
                refrigeratorIdMap[newProductField] = null // 맵에 null 저장
            }
        }

        // 저장 타입 선택 (동적 스피너 설정)
        val storageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, storageTypes)
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        storageTypeSpinner.adapter = storageAdapter



        // 유통기한 설정 (기본으로 비워두고, 필요시 선택할 수 있음)
        expirationDateEditText.setText("") // 기본 유통기한 비워둠

        // 상품 이름, 수량 필드 비워둠
        // ✅ [수정] 상품 이름, 수량 필드에 전달된 값 반영
        productNameEditText.setText(productName)
        quantityEditText.setText(quantity.toString())

        storageTypeSpinner.setSelection(0) // 기본값으로 "냉장" 설정

        // 유통기한 선택
        expirationDateEditText.setOnClickListener { showDatePickerDialog(expirationDateEditText) }

        // 필드가 표시되어야 할 경우에만 추가
        if (isVisible) {
            binding.productFieldsContainer.addView(newProductField)
        }
        productFields.add(newProductField as LinearLayout) // 동적 필드 리스트에 추가
    }

    //토큰 가져오기
    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }
    private fun sendIngredientsToServer(ingredientRequests: List<IngredientRequestDTO>) {
        val token = getToken() ?: run {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val call =
            ApiClient.getIngredientApi().uploadIngredients("Bearer $token", ingredientRequests)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "재료가 업로드되었습니다!", Toast.LENGTH_SHORT).show()
                    dismiss()

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Log.e("ProductUpload", "Response code: ${response.code()}, message: ${response.message()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductUpload", "Error body: $errorBody")

                    val errorMessage = "재료 업로드 실패: ${response.message()}"
                    try {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("ProductUpload", "Toast 표시 중 오류 발생", e)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "네트워크 오류: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}