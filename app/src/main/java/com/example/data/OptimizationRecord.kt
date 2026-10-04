package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "optimization_records")
data class OptimizationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "TURBO_BOOST", "DEEP_CLEAN", "PING_STABILIZE", "GAME_LAUNCH"
    val freedMemoryMb: Int,
    val pingBeforeMs: Int,
    val pingAfterMs: Int,
    val batteryTemp: Float,
    val detailMessage: String
)
