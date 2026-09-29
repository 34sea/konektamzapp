package com.example.konekta_mz_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CANDIDATE,
    EMPLOYER,
    ADMIN
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val phone: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val skills: String = "",
    val experience: String = "",
    val companyName: String = "",
    val companyDescription: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
