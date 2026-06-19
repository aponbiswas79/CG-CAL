package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grade_scales")
data class GradeScale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val grade: String,         // e.g. "A+", "A", "A-"
    val minMark: Double,       // e.g. 80.0, 75.0, 70.0
    val maxMark: Double,       // e.g. 100.0, 79.99, 74.99
    val gradePoint: Double     // e.g. 4.00, 3.75, 3.50
)
