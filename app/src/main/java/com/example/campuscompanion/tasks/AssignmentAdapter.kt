package com.example.campuscompanion.tasks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscompanion.databinding.ItemAssignmentBinding

class AssignmentAdapter(
    private val items: MutableList<AssignmentItem>,
    private val showDelete: Boolean = true,
    private val onCheckedChange: (AssignmentItem, Boolean) -> Unit,
    private val onDeleteClick: (AssignmentItem) -> Unit = {}
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(val binding: ItemAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvCourse.text = item.courseName
        holder.binding.tvDueDate.text = item.dueDate

        holder.binding.cbDone.setOnCheckedChangeListener(null)
        holder.binding.cbDone.isChecked = (item.status == "done")

        when (item.status) {
            "todo" -> {
                holder.binding.tvStatus.text = "To do"
                holder.binding.tvStatus.setTextColor(Color.parseColor("#B91C1C"))
            }
            "inprogress" -> {
                holder.binding.tvStatus.text = "In progress"
                holder.binding.tvStatus.setTextColor(Color.parseColor("#047857"))
            }
            "done" -> {
                holder.binding.tvStatus.text = "Done"
                holder.binding.tvStatus.setTextColor(Color.parseColor("#6D28D9"))
            }
        }

        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(item, isChecked)
        }

        holder.binding.btnDelete.visibility = if (showDelete) View.VISIBLE else View.GONE
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AssignmentItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}