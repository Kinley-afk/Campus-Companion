package com.example.campuscompanion.emergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscompanion.databinding.ActivityEmergencyBinding

class EmergencyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contacts = listOf(
            ContactItem("SSO", "Campus support contact", "+97517123456", "🛡️"),
            ContactItem("Chief Councilor", "Student support contact", "+97517123457", "🩺"),
            ContactItem("Health Department", "Health services contact", "+97517123458", "🏥")
        )

        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        binding.rvContacts.adapter = ContactAdapter(contacts) { contact ->
            callNumber(contact.phone)
        }

        binding.btnCallEmergency.setOnClickListener {
            callNumber("112") // Bhutan's general emergency number — swap if your uni uses a different one
        }
    }

    private fun callNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }
}