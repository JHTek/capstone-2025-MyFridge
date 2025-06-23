package com.example.refrigeratormanager.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.R

class InstructionAdapter(
    private val instructions: List<Instruction>
) : RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder>() {

    class InstructionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepText: TextView = view.findViewById(R.id.textStep)
        val descriptionText: TextView = view.findViewById(R.id.textDescription)
        val imageStep: ImageView = view.findViewById(R.id.imageStep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instruction, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) {
        val instruction = instructions[position]
        holder.stepText.text = "Step ${instruction.stepNumber}"
        holder.descriptionText.text = instruction.description

        if (!instruction.photoUrl.isNullOrEmpty()) {
            holder.imageStep.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(instruction.photoUrl)
                .into(holder.imageStep)
        } else {
            holder.imageStep.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = instructions.size
}
