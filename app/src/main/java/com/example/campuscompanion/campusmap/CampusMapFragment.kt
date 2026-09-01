package com.example.campuscompanion.campusmap

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscompanion.databinding.FragmentCampusMapBinding
import com.google.firebase.firestore.FirebaseFirestore

class CampusMapFragment : Fragment() {

    private var _binding: FragmentCampusMapBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: LocationAdapter
    private var allLocations = listOf<LocationItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCampusMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadLocations()
    }

    private fun setupRecyclerView() {
        adapter = LocationAdapter(mutableListOf())
        binding.rvLocations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLocations.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filtered = if (query.isEmpty()) {
                    allLocations
                } else {
                    allLocations.filter { it.name.lowercase().contains(query) }
                }
                adapter.updateData(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadLocations() {
        firestore.collection("locations")
            .get()
            .addOnSuccessListener { result ->
                allLocations = result.documents.mapNotNull { doc ->
                    doc.toObject(LocationItem::class.java)?.apply { id = doc.id }
                }
                adapter.updateData(allLocations)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load locations", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}