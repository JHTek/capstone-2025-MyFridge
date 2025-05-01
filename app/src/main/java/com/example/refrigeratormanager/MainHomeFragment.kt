package com.example.refrigeratormanager

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentMainHomeBinding

class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        // 체크리스트 미리보기 로드
        loadChecklistPreview()

        // 바코드 이동
        binding.btnBarcode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BarcodeFragment())
                .addToBackStack(null)
                .commit()
        }

        // 카메라 이동
        binding.btnCamera.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        // 유통기한 더보기 이동


        // 체크리스트 추가 이동
        binding.btnAddChecklist.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChecklistFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun loadChecklistPreview() {
        val sharedPreferences = requireContext().getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        val items = sharedPreferences.getString("checklist", "") ?: ""

        val previewContainer = binding.checklistPreviewContainer
        previewContainer.removeAllViews()

        if (items.isNotEmpty()) {
            items.split("#").forEach { item ->
                val parts = item.split("|")
                if (parts.size == 2) {
                    val text = parts[0]
                    val checked = parts[1].toBoolean()

                    val itemLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 12) // 하단 간격 12dp
                        }
                    }

                    val checkBox = CheckBox(requireContext()).apply {
                        this.text = text
                        this.isChecked = checked
                        this.setTextColor(Color.BLACK)
                        this.textSize = 20f // 텍스트 크기
                    }

                    itemLayout.addView(checkBox)
                    previewContainer.addView(itemLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
