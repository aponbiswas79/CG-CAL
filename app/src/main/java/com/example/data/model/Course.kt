package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semesterId: Int,
    val userId: Int,
    val name: String,
    val creditHours: Double,
    val grade: String,
    val gradePoint: Double
)
