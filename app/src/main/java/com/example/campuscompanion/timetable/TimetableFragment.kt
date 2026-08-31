package com.example.campuscompanion.timetable

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscompanion.databinding.FragmentTimetableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    private var selectedDay = "Mon"
    private val dayTabViews = mutableListOf<TextView>()

    private lateinit var adapter: ClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        buildDayTabs()
        loadClasses()

        binding.btnAddClass.setOnClickListener {
            showAddClassDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ClassAdapter(mutableListOf()) { classItem ->
            deleteClass(classItem)
        }
        binding.rvClasses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClasses.adapter = adapter
    }

    private fun buildDayTabs() {
        binding.dayTabsContainer.removeAllViews()
        dayTabViews.clear()

        for (day in days) {
            val tab = TextView(requireContext()).apply {
                text = day
                setPadding(40, 20, 40, 20)
                textSize = 14f
                isSelected = (day == selectedDay)
                setTextColor(resources.getColorStateList(
                    com.example.campuscompanion.R.color.text_pill_selector, null
                ))
                background = resources.getDrawable(
                    com.example.campuscompanion.R.drawable.bg_pill_selector, null
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 16 }

                setOnClickListener {
                    selectedDay = day
                    dayTabViews.forEach { it.isSelected = false }
                    isSelected = true
                    loadClasses()
                }
            }
            dayTabViews.add(tab)
            binding.dayTabsContainer.addView(tab)
        }
    }

    private fun loadClasses() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("classes")
            .whereEqualTo("ownerId", uid)
            .whereEqualTo("day", selectedDay)
            .get()
            .addOnSuccessListener { result ->
                val classes = result.documents.mapNotNull { doc ->
                    doc.toObject(ClassItem::class.java)?.apply { id = doc.id }
                }.sortedBy { it.startTime }

                adapter.updateData(classes)
                binding.tvEmptyState.visibility =
                    if (classes.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load timetable", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteClass(classItem: ClassItem) {
        firestore.collection("classes").document(classItem.id)
            .delete()
            .addOnSuccessListener {
                loadClasses()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddClassDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.example.campuscompanion.R.layout.dialog_add_class, null)

        val etCourseName = dialogView.findViewById<android.widget.EditText>(
            com.example.campuscompanion.R.id.etCourseName
        )
        val etStartTime = dialogView.findViewById<android.widget.EditText>(
            com.example.campuscompanion.R.id.etStartTime
        )
        val etEndTime = dialogView.findViewById<android.widget.EditText>(
            com.example.campuscompanion.R.id.etEndTime
        )
        val etLocation = dialogView.findViewById<android.widget.EditText>(
            com.example.campuscompanion.R.id.etLocation
        )
        val etInstructor = dialogView.findViewById<android.widget.EditText>(
            com.example.campuscompanion.R.id.etInstructor
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Add Class — $selectedDay")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val uid = auth.currentUser?.uid ?: return@setPositiveButton

                val newClass = ClassItem(
                    ownerId = uid,
                    day = selectedDay,
                    courseName = etCourseName.text.toString().trim(),
                    startTime = etStartTime.text.toString().trim(),
                    endTime = etEndTime.text.toString().trim(),
                    location = etLocation.text.toString().trim(),
                    instructor = etInstructor.text.toString().trim()
                )

                if (newClass.courseName.isEmpty()) {
                    Toast.makeText(requireContext(), "Course name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                firestore.collection("classes")
                    .add(newClass)
                    .addOnSuccessListener {
                        loadClasses()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add class", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}