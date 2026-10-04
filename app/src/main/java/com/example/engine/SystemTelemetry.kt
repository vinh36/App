package com.example.engine

data class RamTelemetry(
    val totalMb: Long = 0L,
    val availMb: Long = 0L,
    val usedMb: Long = 0L,
    val usedPercent: Int = 0,
    val isCritical: Boolean = false
)

data class StorageTelemetry(
    val totalGb: Double = 0.0,
    val freeGb: Double = 0.0,
    val usedGb: Double = 0.0,
    val usedPercent: Int = 0
)

data class BatteryTelemetry(
    val levelPercent: Int = 100,
    val tempCelsius: Float = 32.0f,
    val isCharging: Boolean = false,
    val healthStatus: String = "Tốt"
)

data class NetworkTelemetry(
    val isConnected: Boolean = true,
    val connectionType: String = "Wi-Fi",
    val currentPingMs: Int = 24,
    val packetQuality: String = "Cực mượt",
    val ipAddress: String = "192.168.1.1"
)

data class DisplayCpuTelemetry(
    val refreshRateHz: Int = 60,
    val screenResolution: String = "1080 x 2400",
    val cpuCores: Int = 8,
    val thermalThrottlingEstimate: String = "Mát mẻ (Không nghẽn)"
)

data class CacheScanResult(
    val totalCacheBytes: Long = 0L,
    val appCacheBytes: Long = 0L,
    val tempJunkBytes: Long = 0L,
    val logJunkBytes: Long = 0L,
    val formattedTotalSize: String = "0 MB"
)

data class PingNode(
    val id: String,
    val name: String,
    val host: String,
    val location: String,
    val pingMs: Int = 0,
    val isTesting: Boolean = false,
    val status: String = "Sẵn sàng"
)

data class BoostProgress(
    val isBoosting: Boolean = false,
    val currentStepIndex: Int = 0,
    val currentStepTitle: String = "",
    val progressPercent: Float = 0f,
    val freedMemoryMb: Int = 0,
    val isCompleted: Boolean = false
)
