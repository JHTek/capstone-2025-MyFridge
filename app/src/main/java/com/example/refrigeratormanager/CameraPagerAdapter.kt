package com.example.refrigeratormanager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.refrigeratormanager.BarcodeFragment
import com.example.refrigeratormanager.CameraFragment
import com.example.refrigeratormanager.ManualInputFragment

class CameraPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // 바코드, 카메라, 직접 입력 총 3개

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BarcodeFragment() // 바코드 스캔 화면
            1 -> CameraFragment()  // 카메라 촬영 화면
            2 -> Fragment() // 직접 입력 화면은 다이얼로그로 대체
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
