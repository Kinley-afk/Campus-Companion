package com.example.campuscompanion.events

data class EventItem(
    var id: String = "",
    val title: String = "",
    val organizer: String = "",
    val dateText: String = "",
    val location: String = "",
    val type: String = "event", // "event" | "news"
    val featured: Boolean = false
)