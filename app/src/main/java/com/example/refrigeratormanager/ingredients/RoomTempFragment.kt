package com.example.refrigeratormanager.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.refrigeratormanager.databinding.FragmentRoomTempBinding

class RoomTempFragment : BaseIngredientFragment<FragmentRoomTempBinding>() {
    override val storageIndex = 2 // 실온
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoomTempBinding
            = FragmentRoomTempBinding::inflate

    companion object {
        fun newInstance(refrigeratorId: Int, refrigeratorName: String): RoomTempFragment {
            val fragment = RoomTempFragment()
            val args = Bundle()
            args.putInt("refrigerator_id", refrigeratorId)
            args.putString("refrigerator_name", refrigeratorName)
            fragment.arguments = args
            return fragment
        }
    }
}

