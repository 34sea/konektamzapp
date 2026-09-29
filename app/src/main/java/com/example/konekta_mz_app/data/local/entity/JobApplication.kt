package com.example.konekta_mz_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ApplicationStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

@Entity(tableName = "job_applications")
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobId: Long,
    val jobTitle: String,
    val candidateId: Long,
    val candidateName: String,
    val employerId: Long,
    val coverLetter: String = "",
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
