package com.example.campuscompanion.timetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscompanion.databinding.ItemClassBinding

class ClassAdapter(
    private val items: MutableList<ClassItem>,
    private val onDeleteClick: (ClassItem) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(val binding: ItemClassBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvCourseName.text = item.courseName
        holder.binding.tvTime.text = "${item.startTime} – ${item.endTime}"
        holder.binding.tvLocation.text = item.location
        holder.binding.tvInstructor.text = item.instructor

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ClassItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}