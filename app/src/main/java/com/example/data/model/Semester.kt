package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semesters")
data class Semester(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String, // e.g., "Year 1 Term I", "Year 1 Term II"
    val gpa: Double = 0.0,
    val totalCredits: Double = 0.0
)
