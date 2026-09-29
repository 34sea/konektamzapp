package com.example.konekta_mz_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.ApplicationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_applications ORDER BY createdAt DESC")
    fun getAllApplications(): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE candidateId = :candidateId ORDER BY createdAt DESC")
    fun getApplicationsByCandidate(candidateId: Long): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE employerId = :employerId ORDER BY createdAt DESC")
    fun getApplicationsByEmployer(employerId: Long): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE jobId = :jobId")
    suspend fun getApplicationsForJob(jobId: Long): List<JobApplication>

    @Query("SELECT * FROM job_applications WHERE candidateId = :candidateId AND jobId = :jobId LIMIT 1")
    suspend fun getApplication(candidateId: Long, jobId: Long): JobApplication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplication): Long

    @Update
    suspend fun updateApplication(application: JobApplication)

    @Delete
    suspend fun deleteApplication(application: JobApplication)

    @Query("UPDATE job_applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: Long, status: ApplicationStatus)
}
