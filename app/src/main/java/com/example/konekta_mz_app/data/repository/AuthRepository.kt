package com.example.konekta_mz_app.data.repository

import com.example.konekta_mz_app.data.local.dao.UserDao
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.login(email, password)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Email ou senha incorretos"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        phone: String = "",
        location: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        companyName: String = "",
        companyDescription: String = "",
        profileImagePath: String = ""
    ): Result<Long> {
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            return Result.failure(Exception("Email já registado"))
        }

        val user = User(
            name = name,
            email = email,
            password = password,
            role = role,
            phone = phone,
            location = location,
            latitude = latitude,
            longitude = longitude,
            companyName = companyName,
            companyDescription = companyDescription,
            profileImagePath = profileImagePath
        )
        val id = userDao.insertUser(user)
        return Result.success(id)
    }

    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    fun getUsersByRole(role: UserRole): Flow<List<User>> = userDao.getUsersByRole(role)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun getUserCount(): Int = userDao.getUserCount()
}
