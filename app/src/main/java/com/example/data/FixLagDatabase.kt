package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OptimizationRecord::class, GameApp::class],
    version = 1,
    exportSchema = false
)
abstract class FixLagDatabase : RoomDatabase() {
    abstract fun optimizationDao(): OptimizationDao

    companion object {
        @Volatile
        private var INSTANCE: FixLagDatabase? = null

        fun getDatabase(context: Context): FixLagDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FixLagDatabase::class.java,
                    "fix_lag_turbo.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
