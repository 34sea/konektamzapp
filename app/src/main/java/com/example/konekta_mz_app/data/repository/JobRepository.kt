package com.example.konekta_mz_app.data.repository

import com.example.konekta_mz_app.data.local.dao.JobApplicationDao
import com.example.konekta_mz_app.data.local.dao.JobOfferDao
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.JobOffer
import kotlinx.coroutines.flow.Flow

class JobRepository(
    private val jobOfferDao: JobOfferDao,
    private val jobApplicationDao: JobApplicationDao
) {

    fun getAllActiveOffers(): Flow<List<JobOffer>> = jobOfferDao.getAllActiveOffers()

    fun getAllOffers(): Flow<List<JobOffer>> = jobOfferDao.getAllOffers()

    suspend fun getOfferById(id: Long): JobOffer? = jobOfferDao.getOfferById(id)

    fun getOffersByEmployer(employerId: Long): Flow<List<JobOffer>> =
        jobOfferDao.getOffersByEmployer(employerId)

    fun getOffersByCategory(category: String): Flow<List<JobOffer>> =
        jobOfferDao.getOffersByCategory(category)

    fun searchOffers(query: String): Flow<List<JobOffer>> = jobOfferDao.searchOffers(query)

    suspend fun createOffer(offer: JobOffer): Long = jobOfferDao.insertOffer(offer)

    suspend fun updateOffer(offer: JobOffer) = jobOfferDao.updateOffer(offer)

    suspend fun deleteOffer(offer: JobOffer) = jobOfferDao.deleteOffer(offer)

    suspend fun getActiveOfferCount(): Int = jobOfferDao.getActiveOfferCount()

    // Applications
    fun getAllApplications(): Flow<List<JobApplication>> = jobApplicationDao.getAllApplications()

    fun getApplicationsByCandidate(candidateId: Long): Flow<List<JobApplication>> =
        jobApplicationDao.getApplicationsByCandidate(candidateId)

    fun getApplicationsByEmployer(employerId: Long): Flow<List<JobApplication>> =
        jobApplicationDao.getApplicationsByEmployer(employerId)

    suspend fun getApplicationsForJob(jobId: Long): List<JobApplication> =
        jobApplicationDao.getApplicationsForJob(jobId)

    suspend fun applyForJob(application: JobApplication): Long =
        jobApplicationDao.insertApplication(application)

    suspend fun updateApplication(application: JobApplication) =
        jobApplicationDao.updateApplication(application)

    suspend fun updateApplicationStatus(id: Long, status: com.example.konekta_mz_app.data.local.entity.ApplicationStatus) =
        jobApplicationDao.updateApplicationStatus(id, status)

    suspend fun getApplication(candidateId: Long, jobId: Long): JobApplication? =
        jobApplicationDao.getApplication(candidateId, jobId)
}
