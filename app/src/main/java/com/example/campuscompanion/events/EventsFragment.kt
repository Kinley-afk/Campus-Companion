package com.example.campuscompanion.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscompanion.R
import com.example.campuscompanion.databinding.FragmentEventsBinding
import com.google.firebase.firestore.FirebaseFirestore

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()

    private val filters = listOf(
        "all" to "All",
        "event" to "Events",
        "news" to "News"
    )
    private var selectedFilter = "all"
    private val filterTabViews = mutableListOf<TextView>()

    private lateinit var adapter: EventAdapter
    private var allEvents = listOf<EventItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        buildFilterTabs()
        loadEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(mutableListOf())
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
    }

    private fun buildFilterTabs() {
        binding.filterTabsContainer.removeAllViews()
        filterTabViews.clear()

        for ((value, label) in filters) {
            val tab = TextView(requireContext()).apply {
                text = label
                setPadding(40, 20, 40, 20)
                textSize = 14f
                isSelected = (value == selectedFilter)
                setTextColor(resources.getColorStateList(R.color.text_pill_selector, null))
                background = resources.getDrawable(R.drawable.bg_pill_selector, null)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 12 }

                setOnClickListener {
                    selectedFilter = value
                    filterTabViews.forEach { it.isSelected = false }
                    isSelected = true
                    applyFilter()
                }
            }
            filterTabViews.add(tab)
            binding.filterTabsContainer.addView(tab)
        }
    }

    private fun loadEvents() {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                allEvents = result.documents.mapNotNull { doc ->
                    doc.toObject(EventItem::class.java)?.apply { id = doc.id }
                }
                showFeatured()
                applyFilter()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showFeatured() {
        val featured = allEvents.firstOrNull { it.featured }
        if (featured != null) {
            binding.featuredBanner.visibility = View.VISIBLE
            binding.tvFeaturedTitle.text = featured.title
            binding.tvFeaturedDate.text = "${featured.dateText} · ${featured.location}"
        } else {
            binding.featuredBanner.visibility = View.GONE
        }
    }

    private fun applyFilter() {
        val filtered = if (selectedFilter == "all") {
            allEvents
        } else {
            allEvents.filter { it.type == selectedFilter }
        }

        adapter.updateData(filtered)
        binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}