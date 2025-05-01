package com.example.refrigeratormanager

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentChecklistBinding

class ChecklistFragment : Fragment() {
    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private val checklistItems = mutableListOf<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)


        // 항목 추가 버튼
        binding.btnAddItem.setOnClickListener {
            val itemText = binding.editChecklist.text.toString().trim()
            if (itemText.isNotEmpty()) {
                addChecklistItem(itemText, false)
                binding.editChecklist.setText("")
                saveChecklist()
            } else {
                Toast.makeText(requireContext(), "항목을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 옵션 버튼 (팝업 메뉴)
        binding.btnOption.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menuInflater.inflate(R.menu.menu_checklist_options, popup.menu)


            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_check_all -> {
                        checklistItems.forEach { it.isChecked = true }
                        saveChecklist()
                        true
                    }
                    R.id.action_uncheck_all -> {
                        checklistItems.forEach { it.isChecked = false }
                        saveChecklist()
                        true
                    }
                    R.id.action_delete_all -> {
                        binding.checklistContainer.removeAllViews()
                        checklistItems.clear()
                        saveChecklist()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        loadChecklist()
        return binding.root
    }

    private fun addChecklistItem(text: String, checked: Boolean) {
        val checkBox = CheckBox(requireContext()).apply {
            this.text = text
            this.isChecked = checked
            this.textSize = 25f
            setTextColor(resources.getColor(android.R.color.black, null))

            // 체크 상태 변경 시 자동 저장
            setOnCheckedChangeListener { _, _ ->
                saveChecklist()
            }

            // 길게 눌러 수정 다이얼로그
            setOnLongClickListener {
                showEditDialog(this)
                true
            }
        }
        binding.checklistContainer.addView(checkBox)
        checklistItems.add(checkBox)
    }

    private fun showEditDialog(checkBox: CheckBox) {
        val editText = EditText(requireContext()).apply {
            setText(checkBox.text)
            setSelection(text.length)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("항목 수정")
            .setView(editText)
            .setPositiveButton("수정") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    checkBox.text = newText
                    saveChecklist()
                } else {
                    Toast.makeText(requireContext(), "내용이 비어 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun saveChecklist() {
        val sharedPreferences = requireContext().getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val items = checklistItems.joinToString("#") { "${it.text}|${it.isChecked}" }
        editor.putString("checklist", items)
        editor.apply()
    }

    private fun loadChecklist() {
        val sharedPreferences = requireContext().getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        val items = sharedPreferences.getString("checklist", "") ?: ""
        if (items.isNotEmpty()) {
            items.split("#").forEach {
                val parts = it.split("|")
                if (parts.size == 2) {
                    val text = parts[0]
                    val checked = parts[1].toBoolean()
                    addChecklistItem(text, checked)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
