package com.example.konekta_mz_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.konekta_mz_app.data.local.entity.JobOffer
import kotlinx.coroutines.flow.Flow

@Dao
interface JobOfferDao {
    @Query("SELECT * FROM job_offers WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveOffers(): Flow<List<JobOffer>>

    @Query("SELECT * FROM job_offers ORDER BY createdAt DESC")
    fun getAllOffers(): Flow<List<JobOffer>>

    @Query("SELECT * FROM job_offers WHERE id = :id")
    suspend fun getOfferById(id: Long): JobOffer?

    @Query("SELECT * FROM job_offers WHERE employerId = :employerId ORDER BY createdAt DESC")
    fun getOffersByEmployer(employerId: Long): Flow<List<JobOffer>>

    @Query("SELECT * FROM job_offers WHERE category = :category AND isActive = 1 ORDER BY createdAt DESC")
    fun getOffersByCategory(category: String): Flow<List<JobOffer>>

    @Query("SELECT * FROM job_offers WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' AND isActive = 1 ORDER BY createdAt DESC")
    fun searchOffers(query: String): Flow<List<JobOffer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: JobOffer): Long

    @Update
    suspend fun updateOffer(offer: JobOffer)

    @Delete
    suspend fun deleteOffer(offer: JobOffer)

    @Query("DELETE FROM job_offers WHERE id = :id")
    suspend fun deleteOfferById(id: Long)

    @Query("SELECT COUNT(*) FROM job_offers WHERE isActive = 1")
    suspend fun getActiveOfferCount(): Int
}
