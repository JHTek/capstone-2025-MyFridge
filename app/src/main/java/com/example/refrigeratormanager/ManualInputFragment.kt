package com.example.refrigeratormanager

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.refrigeratormanager.databinding.FragmentManualInputBinding
// 상품명을 입력하시오 화면
class ManualInputFragment : DialogFragment() {

    private var _binding: FragmentManualInputBinding? = null
    private val binding get() = _binding!!

    interface ManualInputListener {
        fun onProductNameEntered(productName: String)
    }

    private var listener: ManualInputListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ManualInputListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentManualInputBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        binding.btnConfirm.setOnClickListener {
            val productName = binding.etProductName.text.toString().trim()
            if (productName.isNotEmpty()) {
                listener?.onProductNameEntered(productName) // 상품명 전달
                dismiss()
            } else {
                Toast.makeText(requireContext(), "상품명을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}