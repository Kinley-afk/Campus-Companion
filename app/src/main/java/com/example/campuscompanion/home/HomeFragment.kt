package com.example.campuscompanion.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.campuscompanion.R
import com.example.campuscompanion.databinding.FragmentHomeBinding
import com.example.campuscompanion.emergency.EmergencyActivity
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
        binding.btnEmergency.setOnClickListener {
            startActivity(Intent(requireContext(), EmergencyActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}