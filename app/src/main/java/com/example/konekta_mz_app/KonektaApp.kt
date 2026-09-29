package com.example.konekta_mz_app

import android.app.Application
import com.example.konekta_mz_app.data.local.AppDatabase
import com.example.konekta_mz_app.data.repository.AuthRepository
import com.example.konekta_mz_app.data.repository.CategoryRepository
import com.example.konekta_mz_app.data.repository.JobRepository

class KonektaApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val authRepository by lazy { AuthRepository(database.userDao()) }
    val jobRepository by lazy { JobRepository(database.jobOfferDao(), database.jobApplicationDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
}
