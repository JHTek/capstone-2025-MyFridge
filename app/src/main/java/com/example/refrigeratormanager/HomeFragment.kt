package com.example.refrigeratormanager

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.refrigeratormanager.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RefrigeratorViewModel by activityViewModels()

    private lateinit var refrigeratorAdapter: RefrigeratorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        refrigeratorAdapter = RefrigeratorAdapter { refrigerator ->
            val intent = Intent(requireContext(), RefrigeratorDetailActivity::class.java)
            // 냉장고 고유 ID를 전달 (서버에서 식재료 요청 시 필요)
            intent.putExtra("refrigerator_id", refrigerator.id)     // ✅ 이거 추가!
            intent.putExtra("refrigerator_name", refrigerator.name)
            startActivity(intent)
        }
        binding.recyclerViewRefrigerators.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewRefrigerators.adapter = refrigeratorAdapter

        // ViewModel에서 LiveData를 관찰하여 데이터 변경 시 업데이트
        viewModel.refrigeratorList.observe(viewLifecycleOwner) { list ->
            refrigeratorAdapter.submitList(list) // RecyclerView에 데이터 반영
        }

        // 냉장고 추가 버튼 클릭 이벤트
        binding.btnAddRefrigeratorLayout.setOnClickListener {
            val ftoken = getTokenFromSharedPrefs()
            if (ftoken != null) {
                showAddRefrigeratorDialog("Bearer $ftoken")
            } else {
                Toast.makeText(requireContext(), "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 카메라 이동 버튼 클릭
        binding.fabOpenCamera.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
    }

    // RecyclerView 갱신 메서드
    fun updateRefrigeratorList(list: List<Refrigerator>) {
        refrigeratorAdapter.submitList(list) // RecyclerView에 새로운 데이터 설정
    }

    private fun showAddRefrigeratorDialog(token: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("냉장고 추가")

        val input = EditText(requireContext())
        input.hint = "냉장고 이름 입력"
        builder.setView(input)

        builder.setPositiveButton("추가") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.createRefrigerator(name, token)
            }
        }
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTokenFromSharedPrefs(): String? {
        val sharedPreferences = activity?.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences?.getString("JWT_TOKEN", null)
    }
}
