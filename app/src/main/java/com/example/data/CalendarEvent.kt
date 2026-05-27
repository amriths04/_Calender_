package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: "YYYY-MM-DD"
    val title: String,
    val description: String = "",
    val category: String = "Work",
    val timestamp: Long = System.currentTimeMillis()
)
