package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_apps")
data class GameApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isInstalled: Boolean = true,
    val targetFps: Int = 60, // 30, 60, 90, 120
    val resolutionScale: String = "100%", // "75%", "100%", "125%"
    val autoBoost: Boolean = true,
    val gameCategory: String = "Hành động / Bắn súng",
    val lastOptimized: Long = 0L
)
