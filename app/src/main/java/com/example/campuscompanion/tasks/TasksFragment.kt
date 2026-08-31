package com.example.campuscompanion.tasks

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
import com.example.campuscompanion.R
import com.example.campuscompanion.databinding.FragmentTasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // value = what's stored in Firestore, label = what's shown on the pill
    private val filters = listOf(
        "all" to "All",
        "todo" to "To do",
        "inprogress" to "In progress",
        "done" to "Done"
    )
    private var selectedFilter = "all"
    private val filterTabViews = mutableListOf<TextView>()

    private lateinit var adapter: AssignmentAdapter
    private var allAssignments = listOf<AssignmentItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        buildFilterTabs()
        loadAssignments()

        binding.btnAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = AssignmentAdapter(mutableListOf()) { item, isChecked ->
            updateStatus(item, if (isChecked) "done" else "todo")
        }
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = adapter
    }

    private fun buildFilterTabs() {
        binding.filterTabsContainer.removeAllViews()
        filterTabViews.clear()

        for ((value, label) in filters) {
            val tab = TextView(requireContext()).apply {
                text = label
                setPadding(36, 18, 36, 18)
                textSize = 13f
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

    private fun loadAssignments() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("assignments")
            .whereEqualTo("ownerId", uid)
            .get()
            .addOnSuccessListener { result ->
                allAssignments = result.documents.mapNotNull { doc ->
                    doc.toObject(AssignmentItem::class.java)?.apply { id = doc.id }
                }
                applyFilter()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load assignments", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applyFilter() {
        val filtered = if (selectedFilter == "all") {
            allAssignments
        } else {
            allAssignments.filter { it.status == selectedFilter }
        }

        adapter.updateData(filtered)
        binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE

        val leftCount = allAssignments.count { it.status != "done" }
        binding.tvSubtitle.text = "$leftCount tasks left this week"
    }

    private fun updateStatus(item: AssignmentItem, newStatus: String) {
        firestore.collection("assignments").document(item.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                loadAssignments()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_assignment, null)

        val etTitle = dialogView.findViewById<android.widget.EditText>(R.id.etTitle)
        val etCourseName = dialogView.findViewById<android.widget.EditText>(R.id.etCourseName)
        val etDueDate = dialogView.findViewById<android.widget.EditText>(R.id.etDueDate)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Assignment")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val uid = auth.currentUser?.uid ?: return@setPositiveButton

                val newItem = AssignmentItem(
                    ownerId = uid,
                    title = etTitle.text.toString().trim(),
                    courseName = etCourseName.text.toString().trim(),
                    dueDate = etDueDate.text.toString().trim(),
                    status = "todo"
                )

                if (newItem.title.isEmpty()) {
                    Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                firestore.collection("assignments")
                    .add(newItem)
                    .addOnSuccessListener {
                        loadAssignments()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add assignment", Toast.LENGTH_SHORT).show()
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