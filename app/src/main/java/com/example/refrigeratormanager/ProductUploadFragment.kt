package com.example.refrigeratormanager

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.refrigeratormanager.databinding.FragmentProductUploadBinding
import java.util.*

class ProductUploadFragment : DialogFragment() {

    private var _binding: FragmentProductUploadBinding? = null
    private val binding get() = _binding!!

    private var userRefrigerators: List<String> = listOf() // 유저의 냉장고 리스트
    private val storageTypes = listOf("냉장", "냉동", "실온") // 보관 방식 리스트

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentProductUploadBinding.inflate(LayoutInflater.from(context))

        setupUI()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupUI() {
        val productName = arguments?.getString("productName") ?: ""
        binding.productNameEditText.setText(productName)

        binding.quantityEditText.setText("1") // 기본 수량 설정

        loadUserRefrigerators()
        setupStorageTypeSpinner()
        setupEventListeners()
    }

    // 유저의 냉장고 목록을 로드하여 Spinner 설정 //
    private fun loadUserRefrigerators() {
        userRefrigerators = getUserRefrigeratorsFromDatabase()

        if (userRefrigerators.isEmpty()) {
            binding.refrigeratorSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("없음"))
            binding.uploadButton.isEnabled = false // 업로드 버튼 비활성화
        } else {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userRefrigerators)
            binding.refrigeratorSpinner.adapter = adapter
            binding.uploadButton.isEnabled = true // 업로드 버튼 활성화
        }
    }

    // 저장 타입 (냉장, 냉동, 실온) Spinner 설정//
    private fun setupStorageTypeSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, storageTypes)
        binding.storageTypeSpinner.adapter = adapter
    }

    //유저 냉장고 데이터를 가져오는 함수 (실제 데이터베이스 연동 필요) //
    private fun getUserRefrigeratorsFromDatabase(): List<String> {
        return listOf() // 실제 구현 필요
    }

    //유통기한 선택 다이얼로그 표시 //
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            binding.expirationDateEditText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupEventListeners() {
        binding.calendarButton.setOnClickListener { showDatePickerDialog() }

        binding.uploadButton.setOnClickListener { uploadProduct() }

        binding.btnCancel.setOnClickListener { dismiss() } // 취소 버튼 누르면 팝업 닫기
    }

    //제품 업로드 처리 //
    private fun uploadProduct() {
        val selectedRefrigerator = binding.refrigeratorSpinner.selectedItem.toString()
        if (selectedRefrigerator == "없음") {
            Toast.makeText(requireContext(), "냉장고가 없습니다. 제품을 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val productName = binding.productNameEditText.text.toString().trim()
        val quantity = binding.quantityEditText.text.toString().trim().toIntOrNull()
        val expirationDate = binding.expirationDateEditText.text.toString().trim()
        val storageType = binding.storageTypeSpinner.selectedItem.toString()

        if (productName.isEmpty() || quantity == null || expirationDate.isEmpty()) {
            Toast.makeText(requireContext(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "상품이 업로드되었습니다!", Toast.LENGTH_SHORT).show()

        dismiss() // 업로드 후 다이얼로그 닫기
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
