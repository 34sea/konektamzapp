package com.example.konekta_mz_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.konekta_mz_app.data.local.converter.Converters
import com.example.konekta_mz_app.data.local.dao.CategoryDao
import com.example.konekta_mz_app.data.local.dao.JobApplicationDao
import com.example.konekta_mz_app.data.local.dao.JobOfferDao
import com.example.konekta_mz_app.data.local.dao.UserDao
import com.example.konekta_mz_app.data.local.entity.Category
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, JobOffer::class, JobApplication::class, Category::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun jobOfferDao(): JobOfferDao
    abstract fun jobApplicationDao(): JobApplicationDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "konekta_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            // Seed default categories
            val categories = listOf(
                Category(name = "Construção", icon = "🏗️"),
                Category(name = "Agricultura", icon = "🌾"),
                Category(name = "Comércio", icon = "🛒"),
                Category(name = "Serviços", icon = "🔧"),
                Category(name = "Tecnologia", icon = "💻"),
                Category(name = "Educação", icon = "📚"),
                Category(name = "Saúde", icon = "🏥"),
                Category(name = "Transportes", icon = "🚚"),
                Category(name = "Hotelaria", icon = "🏨"),
                Category(name = "Limpeza", icon = "🧹"),
                Category(name = "Segurança", icon = "🔒"),
                Category(name = "Outros", icon = "📋")
            )
            database.categoryDao().insertAll(categories)

            // Seed default admin user
            val admin = User(
                name = "Administrador",
                email = "admin@konekta.co.mz",
                password = "admin123",
                role = UserRole.ADMIN,
                phone = "+258 84 000 0000",
                location = "Maputo, Moçambique"
            )
            database.userDao().insertUser(admin)
        }
    }
}
