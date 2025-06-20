package com.example.refrigeratormanager.ingredients


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup

import com.example.refrigeratormanager.databinding.FragmentRefrigeratedBinding

class RefrigeratedFragment : BaseIngredientFragment<FragmentRefrigeratedBinding>() {
    override val storageIndex = 0 // 냉장
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRefrigeratedBinding
            = FragmentRefrigeratedBinding::inflate


    companion object {
        fun newInstance(refrigeratorId: Int, refrigeratorName: String): RefrigeratedFragment {
            val fragment = RefrigeratedFragment()
            val args = Bundle()
            args.putInt("refrigerator_id", refrigeratorId)
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }
}

