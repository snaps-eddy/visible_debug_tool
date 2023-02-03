package com.eddy.debuglibrary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eddy.debuglibrary.data.log.entity.Log
import com.eddy.debuglibrary.data.log.entity.LogConverters
import com.eddy.debuglibrary.data.log.local.dao.LogDao

@Database(entities = [Log::class], version = 1, exportSchema = true)
@TypeConverters(LogConverters::class)
internal abstract class AppDatabase: RoomDatabase() {

    abstract fun logDao(): LogDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        private fun buildDatabase(context: Context): AppDatabase {

            return Room.databaseBuilder(context, AppDatabase::class.java, "logs-db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                }).build()
        }
    }


}