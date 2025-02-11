package com.example.refrigeratormanager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RefrigeratorPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RefrigeratedFragment()  // 냉장
            1 -> FrozenFragment()       // 냉동
            2 -> RoomTempFragment()     // 실온
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
