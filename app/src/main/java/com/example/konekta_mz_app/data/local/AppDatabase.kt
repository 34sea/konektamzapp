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
    version = 3,
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
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database)
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    ensureAdminExists(database)
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
                Category(name = "Construção", icon = "construction"),
                Category(name = "Agricultura", icon = "agriculture"),
                Category(name = "Comércio", icon = "commerce"),
                Category(name = "Serviços", icon = "services"),
                Category(name = "Tecnologia", icon = "tech"),
                Category(name = "Educação", icon = "education"),
                Category(name = "Saúde", icon = "health"),
                Category(name = "Transportes", icon = "transport"),
                Category(name = "Hotelaria", icon = "hotel"),
                Category(name = "Limpeza", icon = "cleaning"),
                Category(name = "Segurança", icon = "security"),
                Category(name = "Outros", icon = "other")
            )
            database.categoryDao().insertAll(categories)

            // Seed default admin user
            ensureAdminExists(database)
        }

        private suspend fun ensureAdminExists(database: AppDatabase) {
            val existingAdmin = database.userDao().getFirstAdmin()
            if (existingAdmin == null) {
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
}
