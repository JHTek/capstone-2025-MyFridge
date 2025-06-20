package com.example.refrigeratormanager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.refrigeratormanager.ingredients.FrozenFragment
import com.example.refrigeratormanager.ingredients.RefrigeratedFragment
import com.example.refrigeratormanager.ingredients.RoomTempFragment

class RefrigeratorPagerAdapter(
    activity: AppCompatActivity,
    private val refrigeratorId: Int,
    private val refrigeratorName: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RefrigeratedFragment.newInstance(refrigeratorId, refrigeratorName) // 냉장
            1 -> FrozenFragment.newInstance(refrigeratorId, refrigeratorName)       // 냉동
            2 -> RoomTempFragment.newInstance(refrigeratorId, refrigeratorName) // 실온
            else -> Fragment()
        }
    }
}
