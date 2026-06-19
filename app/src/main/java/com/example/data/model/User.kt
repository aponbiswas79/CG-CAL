package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val username: String,
    val passwordHash: String,
    val registrationDate: Long = System.currentTimeMillis(),
    val lastLoginDate: Long = System.currentTimeMillis(),
    val accountStatus: String = "ACTIVE",
    val school: String = "",
    val discipline: String = "",
    val batch: String = "",
    val isLoggedIn: Boolean = false
)
