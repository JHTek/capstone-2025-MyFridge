package com.example.refrigeratormanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.refrigeratormanager.databinding.ActivityNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val logs = prefs.getString("alert_log", "") ?: ""

        val notificationItems = logs
            .split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { log ->
                val parts = log.split(" - ", limit = 2)
                if (parts.size == 2) NotificationItem(date = parts[0], content = parts[1])
                else null
            }
            .sortedByDescending { item -> sdf.parse(item.date) }
            .toMutableList()

        adapter = NotificationAdapter(notificationItems)
        binding.rvNotificationList.layoutManager = LinearLayoutManager(this)
        binding.rvNotificationList.adapter = adapter

        binding.btnback.setOnClickListener { finish() }
        binding.btnClearAll.setOnClickListener {
            adapter.items.clear()
            adapter.notifyDataSetChanged()
            prefs.edit().remove("alert_log").apply()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: androidx.recyclerview.widget.RecyclerView, vh: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: androidx.recyclerview.widget.RecyclerView.ViewHolder, dir: Int) {
                val position = vh.adapterPosition
                val removedItem = adapter.items[position]
                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                val updatedLog = logs.split("\n").filter { it.isNotBlank() && it != "${removedItem.date} - ${removedItem.content}" }.joinToString("\n")
                prefs.edit().putString("alert_log", updatedLog).apply()
            }
        }).attachToRecyclerView(binding.rvNotificationList)
    }
}
