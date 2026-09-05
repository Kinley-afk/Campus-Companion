package com.example.campuscompanion.campusmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscompanion.databinding.ItemLocationBinding

class LocationAdapter(
    private val items: MutableList<LocationItem>
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvIcon.text = item.icon
        holder.binding.tvName.text = item.name
        holder.binding.tvDetails.text = "${item.category} · ${item.hours}"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<LocationItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}