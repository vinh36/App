package com.example.data

import com.example.engine.CacheScanResult
import com.example.engine.LagFixManager
import com.example.engine.PingNode
import com.example.engine.RamTelemetry
import com.example.engine.StorageTelemetry
import com.example.engine.BatteryTelemetry
import com.example.engine.DisplayCpuTelemetry
import com.example.engine.NetworkTelemetry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SystemRepository(
    private val database: FixLagDatabase,
    private val lagFixManager: LagFixManager
) {
    private val dao = database.optimizationDao()

    val allRecords: Flow<List<OptimizationRecord>> = dao.getAllRecords()
    val allGames: Flow<List<GameApp>> = dao.getAllGames()

    suspend fun initializePreloadedGames() {
        val existing = dao.getAllGames().first()
        if (existing.isEmpty()) {
            val gamesWithInstallState = PreloadedGames.POPULAR_GAMES.map { game ->
                game.copy(isInstalled = lagFixManager.isAppInstalled(game.packageName))
            }
            dao.insertGames(gamesWithInstallState)
        } else {
            // Refresh installation flags
            for (game in existing) {
                val installed = lagFixManager.isAppInstalled(game.packageName)
                if (installed != game.isInstalled) {
                    dao.updateGame(game.copy(isInstalled = installed))
                }
            }
        }
    }

    suspend fun addGame(game: GameApp) {
        dao.insertGame(game)
    }

    suspend fun updateGame(game: GameApp) {
        dao.updateGame(game)
    }

    suspend fun removeGame(packageName: String) {
        dao.deleteGame(packageName)
    }

    suspend fun saveRecord(record: OptimizationRecord) {
        dao.insertRecord(record)
    }

    suspend fun clearHistory() {
        dao.clearAllRecords()
    }

    fun getRamTelemetry(): RamTelemetry = lagFixManager.getRamTelemetry()
    fun getStorageTelemetry(): StorageTelemetry = lagFixManager.getStorageTelemetry()
    fun getBatteryTelemetry(): BatteryTelemetry = lagFixManager.getBatteryTelemetry()
    fun getDisplayCpuTelemetry(): DisplayCpuTelemetry = lagFixManager.getDisplayCpuTelemetry()
    fun getNetworkTelemetry(): NetworkTelemetry = lagFixManager.getNetworkTelemetry()

    suspend fun performTurboPurge(): Int = lagFixManager.performTurboMemoryPurge()
    suspend fun scanCache(): CacheScanResult = lagFixManager.scanCacheSize()
    suspend fun clearCache(): Long = lagFixManager.clearJunkAndCache()
    suspend fun pingHost(host: String): Int = lagFixManager.testServerPing(host)
    fun triggerHaptics(strong: Boolean = false) = lagFixManager.triggerHapticFeedback(strong)
    fun launchGame(packageName: String): Boolean = lagFixManager.launchApp(packageName)
}
