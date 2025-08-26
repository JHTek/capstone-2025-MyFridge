package com.example.refrigeratormanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnback.setOnClickListener { finish() } //뒤로가기 버튼 클릭 시 현재 Activity 종료

        val prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE)//SharedPreferences에서 알림 로그 불러오기
        val logs = prefs.getString("alert_log", "") ?: ""      // (AlarmWorker에서 저장한 기록)


        // 로그 문자열을 리스트로 변환
        // 로그 형식: "2025-08-26 10:00 - 우유 (1개), 유통기한 2025-08-30"
        val notificationItems = logs
            .split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { log ->
                val parts = log.split(" - ", limit = 2)
                if (parts.size == 2) {
                    NotificationItem(date = parts[0], content = parts[1])
                } else null
            }
            .sortedByDescending { it.date } // 최근 날짜순 정렬
            .toMutableList()

        // 어댑터 생성 후 RecyclerView 연결
        adapter = NotificationAdapter(notificationItems)
        binding.rvNotificationList.layoutManager = LinearLayoutManager(this)
        binding.rvNotificationList.adapter = adapter

        // 스와이프 삭제
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val position = vh.adapterPosition
                val removedItem = adapter.items[position]

                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)

                val updatedLog = logs
                    .split("\n")
                    .filter { it.isNotBlank() && !it.contains(removedItem.content) }
                    .joinToString("\n")

                prefs.edit().putString("alert_log", updatedLog).apply()
            }
        }).attachToRecyclerView(binding.rvNotificationList)
    }
}