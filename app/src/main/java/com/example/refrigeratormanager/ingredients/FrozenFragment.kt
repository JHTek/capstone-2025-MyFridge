package com.example.refrigeratormanager.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.refrigeratormanager.databinding.FragmentFrozenBinding

class FrozenFragment : BaseIngredientFragment<FragmentFrozenBinding>() {
    override val storageIndex = 1 // 냉동
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFrozenBinding
            = FragmentFrozenBinding::inflate

    companion object {
        fun newInstance(refrigeratorId: Int, refrigeratorName: String): FrozenFragment {
            val fragment = FrozenFragment()
            val args = Bundle()
            args.putInt("refrigerator_id", refrigeratorId)
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }
}

