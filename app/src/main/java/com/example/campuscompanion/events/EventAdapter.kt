package com.example.campuscompanion.events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscompanion.databinding.ItemEventBinding

class EventAdapter(
    private val items: MutableList<EventItem>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvOrganizer.text = item.organizer
        holder.binding.tvDateLocation.text = "${item.dateText} · ${item.location}"

        if (item.type == "news") {
            holder.binding.tvType.text = "News"
            holder.binding.tvType.setTextColor(Color.parseColor("#047857"))
            holder.binding.tvIcon.text = "📢"
        } else {
            holder.binding.tvType.text = "Event"
            holder.binding.tvType.setTextColor(Color.parseColor("#6D28D9"))
            holder.binding.tvIcon.text = "🗓️"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<EventItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}