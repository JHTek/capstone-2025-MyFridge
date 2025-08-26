package com.example.refrigeratormanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.databinding.ItemNotificationBinding

// RecyclerView에 표시할 데이터 모델
data class NotificationItem(
    val content: String, //알림 내용 (예: "우유 (1개), 유통기한 2025-08-30")
    val date: String    // 알림 발생 시간 (예: "2025-08-26 10:00")
)
//NotificationAdapter는 RecyclerView에 알림 로그(NotificationItem)를 표시하기 위한 어댑터 클래스

//  RecyclerView 어댑터
// RecyclerView는 데이터를 직접 표시하지 않고, 어댑터를 통해 "뷰와 데이터"를 연결
class NotificationAdapter(val items: MutableList<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    //  ViewHolder: RecyclerView의 각 행(row)을 담는 역할
    // ItemNotificationBinding은 XML(item_notification.xml)과 연결된 ViewBinding
    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    // ViewHolder 생성 (레이아웃 inflate)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), // XML → View 객체 변환
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    // ViewHolder와 데이터를 실제로 연결 (바인딩)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position] // 현재 표시할 NotificationItem
        holder.binding.tvContent.text = item.content // 내용 표시
        holder.binding.tvDate.text = item.date       // 날짜 표시
    }

    //  RecyclerView가 아이템 몇 개를 표시해야 하는지 반환
    override fun getItemCount(): Int = items.size
}