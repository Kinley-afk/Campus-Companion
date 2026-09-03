package com.example.campuscompanion.emergency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscompanion.databinding.ItemContactBinding

class ContactAdapter(
    private val items: List<ContactItem>,
    private val onCallClick: (ContactItem) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvIcon.text = item.icon
        holder.binding.tvName.text = item.name
        holder.binding.tvRole.text = item.role
        holder.binding.tvPhone.text = item.phone
        holder.binding.btnCall.setOnClickListener { onCallClick(item) }
    }

    override fun getItemCount(): Int = items.size
}