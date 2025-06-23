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

        val prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE)
        val logString = prefs.getString("alert_log", "") ?: ""

        val logs = logString
            .split("\n")
            .filter { it.isNotBlank() }
            .sortedDescending()

        binding.btnback.setOnClickListener {
            finish()
        }

        val notificationItems = logs.map { log ->
            val parts = log.split(" - ", limit = 2)
            if (parts.size == 2) {
                NotificationItem(content = parts[1], date = parts[0])
            } else {
                NotificationItem(content = log, date = "")
            }
        }.toMutableList()

        adapter = NotificationAdapter(notificationItems)
        binding.rvNotificationList.layoutManager = LinearLayoutManager(this)
        binding.rvNotificationList.adapter = adapter

        // 스와이프 삭제 기능
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedItem = adapter.items[position]

                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)

                // SharedPreferences에서 해당 로그 제거
                val updatedLog = prefs.getString("alert_log", "")
                    ?.split("\n")
                    ?.filter { it.isNotBlank() && !it.contains(removedItem.content) }
                    ?.joinToString("\n") ?: ""

                prefs.edit().putString("alert_log", updatedLog).apply()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvNotificationList)
    }
}
