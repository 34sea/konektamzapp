package com.example.konekta_mz_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_offers")
data class JobOffer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employerId: Long,
    val employerName: String,
    val title: String,
    val description: String,
    val category: String,
    val salary: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val requirements: String = "",
    val benefits: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
