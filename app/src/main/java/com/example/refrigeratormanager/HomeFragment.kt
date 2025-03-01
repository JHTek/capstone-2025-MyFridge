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

    // ViewModel 가져오기
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

        val token = getTokenFromSharedPrefs()

        // RecyclerView 설정
        refrigeratorAdapter = RefrigeratorAdapter { refrigerator ->
            val intent = Intent(requireContext(), RefrigeratorDetailActivity::class.java)
            intent.putExtra("refrigerator_name", refrigerator.name)
            startActivity(intent)
        }
        binding.recyclerViewRefrigerators.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewRefrigerators.adapter = refrigeratorAdapter

        // LiveData 관찰하여 데이터 변경 감지
        viewModel.refrigeratorList.observe(viewLifecycleOwner) { list ->
            refrigeratorAdapter.submitList(list) // UI 업데이트
            // 냉장고 리스트가 변경되었을 때 Toast 표시
        }

        // 냉장고 추가 버튼 클릭 이벤트
        binding.btnAddRefrigeratorLayout.setOnClickListener {
            if (token != null) {
                showAddRefrigeratorDialog(token)  // 토큰을 Dialog로 넘겨서 사용
            } else {
                Toast.makeText(requireContext(), "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 알림 버튼 클릭 이벤트
        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        // 카메라 이동 버튼 클릭 이벤트
        binding.fabOpenCamera.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }
    }

    private fun showAddRefrigeratorDialog(token : String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("냉장고 추가")

        val input = EditText(requireContext())
        input.hint = "냉장고 이름 입력"
        builder.setView(input)

        builder.setPositiveButton("추가") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                val token = "Bearer $token"   // JWT 토큰을 여기에 입력해야 합니다.
                viewModel.createRefrigerator(name, token)
            }
        }
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewRefrigerators.adapter = null // 메모리 누수 방지
        _binding = null
    }

    private fun getTokenFromSharedPrefs(): String? {
        val sharedPreferences = activity?.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences?.getString("JWT_TOKEN", null)
    }
}