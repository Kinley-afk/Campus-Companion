package com.example.campuscompanion.campusmap

data class LocationItem(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val hours: String = "",
    val icon: String = "📍",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)