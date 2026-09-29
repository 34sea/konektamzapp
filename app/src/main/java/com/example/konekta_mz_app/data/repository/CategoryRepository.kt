package com.example.konekta_mz_app.data.repository

import com.example.konekta_mz_app.data.local.dao.CategoryDao
import com.example.konekta_mz_app.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
}
