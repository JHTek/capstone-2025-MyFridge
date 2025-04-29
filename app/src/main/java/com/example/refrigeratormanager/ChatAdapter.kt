package com.example.refrigeratormanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_MINE) {
            R.layout.item_chat_me // 내가 보낸 메시지 레이아웃
        } else {
            R.layout.item_chat_other // 상대방 메시지 레이아웃
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.message
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isMine) VIEW_TYPE_MINE else VIEW_TYPE_OTHER
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
    }

    companion object {
        private const val VIEW_TYPE_MINE = 0
        private const val VIEW_TYPE_OTHER = 1
    }
}
