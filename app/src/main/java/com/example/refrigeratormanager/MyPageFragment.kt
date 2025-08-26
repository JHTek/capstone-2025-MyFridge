package com.example.refrigeratormanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentMyPageBinding
import com.example.refrigeratormanager.SettingsActivity

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        // 닉네임 임시로 설정 데이터베이스 연동 필요 로그인 할 때 닉네임 반영되어야함
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences",0)
        val nickname = sharedPreferences.getString("NICKNAME","사용자")
        binding.tvGreeting.text = "${nickname}님, 안녕하세요!"

        // 내 정보ㅅ
        binding.btnMyInfo.setOnClickListener {
            startActivity(Intent(requireContext(), MyInfoActivity::class.java))
        }
        // 설정
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        //로그아웃 시
        binding.btnLogout.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("app_preferences", 0)
            sharedPreferences.edit().clear().apply() // 모든 로그인 정보 제거

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}