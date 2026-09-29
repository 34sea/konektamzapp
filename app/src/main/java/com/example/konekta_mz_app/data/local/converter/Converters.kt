package com.example.konekta_mz_app.data.local.converter

import androidx.room.TypeConverter
import com.example.konekta_mz_app.data.local.entity.ApplicationStatus
import com.example.konekta_mz_app.data.local.entity.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun fromApplicationStatus(status: ApplicationStatus): String = status.name

    @TypeConverter
    fun toApplicationStatus(value: String): ApplicationStatus = ApplicationStatus.valueOf(value)
}
