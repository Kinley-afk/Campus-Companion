package com.example.campuscompanion.tasks

data class AssignmentItem(
    var id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val courseName: String = "",
    val dueDate: String = "",
    val status: String = "todo" // "todo" | "inprogress" | "done"
)