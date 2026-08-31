package com.example.campuscompanion.timetable

data class ClassItem(
    var id: String = "",
    val ownerId: String = "",
    val day: String = "",
    val courseName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val instructor: String = ""
)