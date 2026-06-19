package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_calculations")
data class SavedCalculation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val courseType: String, // "THEORY" or "LAB"
    val courseName: String,
    val ct1: Double = 0.0,
    val ct2: Double = 0.0,
    val ct3: Double = 0.0,
    val ct4: Double = 0.0,
    val attendance: Double = 0.0,
    val viva: Double = 0.0,
    val finalExam: Double = 0.0,
    val targetGrade: String = "",
    val calculatedGrade: String = "",
    val gradePoint: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
