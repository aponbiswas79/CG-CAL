package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val activityType: String, // "LOGIN", "REGISTER", "LOGOUT", "PROFILE_UPDATE", "CALC_SAVE", "EXPORT"
    val description: String
)
