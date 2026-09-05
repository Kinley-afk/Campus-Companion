package com.example.campuscompanion.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscompanion.R
import com.example.campuscompanion.auth.LoginActivity
import com.example.campuscompanion.databinding.FragmentHomeBinding
import com.example.campuscompanion.emergency.EmergencyActivity
import com.example.campuscompanion.events.EventAdapter
import com.example.campuscompanion.events.EventItem
import com.example.campuscompanion.tasks.AssignmentAdapter
import com.example.campuscompanion.tasks.AssignmentItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var assignmentAdapter: AssignmentAdapter
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDate()
        loadUserName()
        setupNavigation()
        setupPreviewLists()
        loadAssignmentsPreview()
        loadEventsPreview()
    }

    override fun onResume() {
        super.onResume()
        loadAssignmentsPreview()
        loadEventsPreview()
    }

    private fun setDate() {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())
    }

    private fun loadUserName() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            binding.tvGreeting.text = "Hey there 👋"
            return
        }

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                val firstName = name?.trim()?.split(" ")?.firstOrNull() ?: "there"
                binding.tvGreeting.text = "Hey, $firstName 👋"
            }
            .addOnFailureListener {
                binding.tvGreeting.text = "Hey there 👋"
            }
    }

    private fun setupPreviewLists() {
        assignmentAdapter = AssignmentAdapter(
            items = mutableListOf(),
            showDelete = false,
            onCheckedChange = { _, _ -> }
        )
        binding.rvAssignmentsPreview.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignmentsPreview.adapter = assignmentAdapter

        eventAdapter = EventAdapter(mutableListOf())
        binding.rvEventsPreview.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEventsPreview.adapter = eventAdapter
    }

    private fun loadAssignmentsPreview() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("assignments")
            .whereEqualTo("ownerId", uid)
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.mapNotNull { doc ->
                    doc.toObject(AssignmentItem::class.java)?.apply { id = doc.id }
                }
                    .filter { it.status != "done" }
                    .take(3)

                assignmentAdapter.updateData(items)
            }
    }

    private fun loadEventsPreview() {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.mapNotNull { doc ->
                    doc.toObject(EventItem::class.java)?.apply { id = doc.id }
                }.take(3)

                eventAdapter.updateData(items)
            }
    }

    private fun setupNavigation() {
        binding.actionTimetable.setOnClickListener {
            findNavController().navigate(R.id.timetableFragment)
        }
        binding.actionAssignments.setOnClickListener {
            findNavController().navigate(R.id.tasksFragment)
        }
        binding.actionMap.setOnClickListener {
            findNavController().navigate(R.id.campusMapFragment)
        }
        binding.actionEvents.setOnClickListener {
            findNavController().navigate(R.id.eventsFragment)
        }
        binding.tvViewAllAssignments.setOnClickListener {
            findNavController().navigate(R.id.tasksFragment)
        }
        binding.tvViewAllEvents.setOnClickListener {
            findNavController().navigate(R.id.eventsFragment)
        }
        binding.btnProfile.setOnClickListener { view ->
            showProfileMenu(view)
        }
    }

    private fun showProfileMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.home_profile_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuEmergency -> {
                    startActivity(Intent(requireContext(), EmergencyActivity::class.java))
                    true
                }
                R.id.menuLogout -> {
                    showLogoutConfirmation()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log out?")
            .setMessage("You'll need to sign in again to access your data.")
            .setPositiveButton("Log out") { _, _ ->
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}