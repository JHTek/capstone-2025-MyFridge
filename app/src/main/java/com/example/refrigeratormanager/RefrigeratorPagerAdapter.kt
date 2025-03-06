package com.example.refrigeratormanager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RefrigeratorPagerAdapter(
    activity: AppCompatActivity,
    private val refrigeratorName: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RefrigeratedFragment.newInstance(refrigeratorName) // 냉장
            1 -> FrozenFragment.newInstance(refrigeratorName)       // 냉동
            2 -> RoomTempFragment.newInstance(refrigeratorName) // 실온
            else -> Fragment()
        }
    }
}
