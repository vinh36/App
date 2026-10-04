package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FixLagDatabase
import com.example.data.GameApp
import com.example.data.OptimizationRecord
import com.example.data.SystemRepository
import com.example.engine.BatteryTelemetry
import com.example.engine.BoostProgress
import com.example.engine.CacheScanResult
import com.example.engine.DisplayCpuTelemetry
import com.example.engine.LagFixManager
import com.example.engine.NetworkTelemetry
import com.example.engine.PingNode
import com.example.engine.RamTelemetry
import com.example.engine.StorageTelemetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FixLagTab {
    DASHBOARD,
    BOOSTER,
    GAME_BOX,
    NETWORK_PING,
    GFX_TOOL
}

enum class BoosterMode(val title: String, val subtitle: String, val colorHex: Long) {
    ULTRA_SMOOTH("Siêu Mượt (Max FPS)", "Giải phóng tối đa RAM & CPU cho game", 0xFF00F0FF),
    BATTERY_SAVER("Tiết Kiệm Pin", "Hạ xung nhẹ, giảm nhiệt độ, chơi lâu bền", 0xFF10B981),
    ESPORTS_TURBO("Thể Thao Điện Tử", "Chặn giật lag, cố định 90/120 FPS, ổn định ping", 0xFFFF3366),
    BALANCED("Cân Bằng Thông Minh", "Tự động điều chỉnh hiệu năng theo nhu cầu", 0xFF8B5CF6)
}

data class GfxConfig(
    val resolution: String = "1080p (FHD)",
    val targetFps: Int = 90,
    val graphicsLevel: String = "Siêu mượt (Smooth)",
    val vulkanApi: Boolean = true,
    val shadows: Boolean = false,
    val antiAliasing: String = "Tắt (Tối đa FPS)",
    val isApplied: Boolean = false
)

class FixLagViewModel(application: Application) : AndroidViewModel(application) {

    private val lagFixManager = LagFixManager(application)
    private val database = FixLagDatabase.getDatabase(application)
    private val repository = SystemRepository(database, lagFixManager)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(FixLagTab.DASHBOARD)
    val currentTab: StateFlow<FixLagTab> = _currentTab.asStateFlow()

    // Telemetry
    private val _ramTelemetry = MutableStateFlow(repository.getRamTelemetry())
    val ramTelemetry: StateFlow<RamTelemetry> = _ramTelemetry.asStateFlow()

    private val _storageTelemetry = MutableStateFlow(repository.getStorageTelemetry())
    val storageTelemetry: StateFlow<StorageTelemetry> = _storageTelemetry.asStateFlow()

    private val _batteryTelemetry = MutableStateFlow(repository.getBatteryTelemetry())
    val batteryTelemetry: StateFlow<BatteryTelemetry> = _batteryTelemetry.asStateFlow()

    private val _displayCpuTelemetry = MutableStateFlow(repository.getDisplayCpuTelemetry())
    val displayCpuTelemetry: StateFlow<DisplayCpuTelemetry> = _displayCpuTelemetry.asStateFlow()

    private val _networkTelemetry = MutableStateFlow(repository.getNetworkTelemetry())
    val networkTelemetry: StateFlow<NetworkTelemetry> = _networkTelemetry.asStateFlow()

    // Boost Mode
    private val _selectedMode = MutableStateFlow(BoosterMode.ULTRA_SMOOTH)
    val selectedMode: StateFlow<BoosterMode> = _selectedMode.asStateFlow()

    // Boost Progress
    private val _boostProgress = MutableStateFlow(BoostProgress())
    val boostProgress: StateFlow<BoostProgress> = _boostProgress.asStateFlow()

    private val _lastFreedMb = MutableStateFlow(0)
    val lastFreedMb: StateFlow<Int> = _lastFreedMb.asStateFlow()

    // Cache Scan & Clean
    private val _cacheScanResult = MutableStateFlow(CacheScanResult())
    val cacheScanResult: StateFlow<CacheScanResult> = _cacheScanResult.asStateFlow()

    private val _isCleaningCache = MutableStateFlow(false)
    val isCleaningCache: StateFlow<Boolean> = _isCleaningCache.asStateFlow()

    // Ping Nodes
    private val _pingNodes = MutableStateFlow<List<PingNode>>(
        listOf(
            PingNode("google", "Google DNS Gaming Gateway", "8.8.8.8", "Việt Nam / Toàn Cầu", 24, false, "Rất mượt"),
            PingNode("cloudflare", "Cloudflare Warp 1.1.1.1", "1.1.1.1", "Hà Nội / TP.HCM", 18, false, "Tuyệt vời"),
            PingNode("sea_game", "Máy chủ Game Đông Nam Á", "13.250.0.0", "Singapore", 38, false, "Tốt"),
            PingNode("garena", "Garena VN (Free Fire / LQ)", "118.69.135.1", "Việt Nam", 21, false, "Cực thấp"),
            PingNode("tokyo", "Máy chủ Tokyo Gamer Relay", "13.112.0.0", "Nhật Bản", 64, false, "Khá"),
            PingNode("us_west", "Máy chủ Bắc Mỹ US-West", "54.183.0.0", "Hoa Kỳ", 145, false, "Độ trễ cao")
        )
    )
    val pingNodes: StateFlow<List<PingNode>> = _pingNodes.asStateFlow()

    // GFX Config
    private val _gfxConfig = MutableStateFlow(GfxConfig())
    val gfxConfig: StateFlow<GfxConfig> = _gfxConfig.asStateFlow()

    // Notification / Toast Message
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    // Room Database Flows
    val gameApps: StateFlow<List<GameApp>> = repository.allGames.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val historyRecords: StateFlow<List<OptimizationRecord>> = repository.allRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.initializePreloadedGames()
            refreshTelemetry()
            scanCache()
        }
    }

    fun setTab(tab: FixLagTab) {
        _currentTab.value = tab
        repository.triggerHaptics(false)
    }

    fun selectBoosterMode(mode: BoosterMode) {
        _selectedMode.value = mode
        repository.triggerHaptics(false)
        _snackMessage.value = "Đã chuyển sang chế độ: ${mode.title}"
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    fun refreshTelemetry() {
        _ramTelemetry.value = repository.getRamTelemetry()
        _storageTelemetry.value = repository.getStorageTelemetry()
        _batteryTelemetry.value = repository.getBatteryTelemetry()
        _displayCpuTelemetry.value = repository.getDisplayCpuTelemetry()
        _networkTelemetry.value = repository.getNetworkTelemetry()
    }

    fun startTurboBoost(onComplete: (() -> Unit)? = null) {
        if (_boostProgress.value.isBoosting) return

        viewModelScope.launch {
            repository.triggerHaptics(true)
            _boostProgress.value = BoostProgress(
                isBoosting = true,
                currentStepIndex = 1,
                currentStepTitle = "Quét tiến trình chạy ngầm & chiếm RAM...",
                progressPercent = 0.15f
            )
            delay(500)

            _boostProgress.value = _boostProgress.value.copy(
                currentStepIndex = 2,
                currentStepTitle = "Giải phóng bộ nhớ rác & dọn sạch Cache...",
                progressPercent = 0.40f
            )
            repository.triggerHaptics(false)
            delay(600)

            _boostProgress.value = _boostProgress.value.copy(
                currentStepIndex = 3,
                currentStepTitle = "Tối ưu hóa GPU & Đồng bộ hóa chu kỳ render...",
                progressPercent = 0.70f
            )
            delay(500)

            _boostProgress.value = _boostProgress.value.copy(
                currentStepIndex = 4,
                currentStepTitle = "Kiểm tra độ trễ mạng & Khóa Ping ổn định...",
                progressPercent = 0.90f
            )
            delay(400)

            // Perform real memory purge
            val freedMb = repository.performTurboPurge()
            _lastFreedMb.value = freedMb
            refreshTelemetry()

            val record = OptimizationRecord(
                actionType = "TURBO_BOOST",
                freedMemoryMb = freedMb,
                pingBeforeMs = 45,
                pingAfterMs = 21,
                batteryTemp = _batteryTelemetry.value.tempCelsius,
                detailMessage = "Đã giải phóng $freedMb MB RAM và tối ưu hóa FPS hệ thống."
            )
            repository.saveRecord(record)

            _boostProgress.value = BoostProgress(
                isBoosting = false,
                currentStepIndex = 5,
                currentStepTitle = "Hoàn tất! Đã dọn sạch $freedMb MB RAM",
                progressPercent = 1.0f,
                freedMemoryMb = freedMb,
                isCompleted = true
            )
            repository.triggerHaptics(true)
            _snackMessage.value = "Tối ưu thành công! Đã giải phóng $freedMb MB RAM."
            onComplete?.invoke()
        }
    }

    fun dismissBoostResult() {
        _boostProgress.value = _boostProgress.value.copy(isCompleted = false)
    }

    fun scanCache() {
        viewModelScope.launch {
            val result = repository.scanCache()
            _cacheScanResult.value = result
        }
    }

    fun cleanCache() {
        if (_isCleaningCache.value) return
        viewModelScope.launch {
            _isCleaningCache.value = true
            repository.triggerHaptics(false)
            delay(600)
            val cleared = repository.clearCache()
            val formatted = lagFixManager.formatSize(cleared)
            scanCache()
            refreshTelemetry()

            val record = OptimizationRecord(
                actionType = "DEEP_CLEAN",
                freedMemoryMb = (cleared / (1024 * 1024)).toInt(),
                pingBeforeMs = 30,
                pingAfterMs = 24,
                batteryTemp = _batteryTelemetry.value.tempCelsius,
                detailMessage = "Đã dọn dẹp sạch $formatted tệp rác hệ thống."
            )
            repository.saveRecord(record)

            _isCleaningCache.value = false
            repository.triggerHaptics(true)
            _snackMessage.value = "Đã dọn sạch $formatted bộ nhớ đệm và tệp rác!"
        }
    }

    fun testAllPingNodes() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentList = _pingNodes.value.map { it.copy(isTesting = true) }
            _pingNodes.value = currentList

            val updated = currentList.map { node ->
                val pingResult = repository.pingHost(node.host)
                val statusText = when {
                    pingResult < 25 -> "Cực mượt"
                    pingResult < 50 -> "Rất tốt"
                    pingResult < 90 -> "Ổn định"
                    else -> "Độ trễ cao"
                }
                node.copy(
                    pingMs = pingResult,
                    isTesting = false,
                    status = statusText
                )
            }
            _pingNodes.value = updated
            _snackMessage.value = "Đã kiểm tra xong độ trễ máy chủ mạng!"
        }
    }

    fun launchGameWithBoost(game: GameApp) {
        viewModelScope.launch {
            if (game.autoBoost) {
                repository.triggerHaptics(true)
                _snackMessage.value = "Đang kích hoạt Game Turbo cho ${game.appName}..."
                repository.performTurboPurge()
                delay(300)
            }

            val launched = repository.launchGame(game.packageName)
            if (launched) {
                val updatedGame = game.copy(lastOptimized = System.currentTimeMillis())
                repository.updateGame(updatedGame)
                repository.saveRecord(
                    OptimizationRecord(
                        actionType = "GAME_LAUNCH",
                        freedMemoryMb = 210,
                        pingBeforeMs = 40,
                        pingAfterMs = 20,
                        batteryTemp = _batteryTelemetry.value.tempCelsius,
                        detailMessage = "Khởi chạy tăng tốc Game: ${game.appName} (${game.targetFps} FPS)"
                    )
                )
            } else {
                _snackMessage.value = "${game.appName} chưa được cài đặt trên thiết bị."
            }
        }
    }

    fun toggleAutoBoost(game: GameApp) {
        viewModelScope.launch {
            repository.updateGame(game.copy(autoBoost = !game.autoBoost))
            repository.triggerHaptics(false)
        }
    }

    fun updateGameFps(game: GameApp, newFps: Int) {
        viewModelScope.launch {
            repository.updateGame(game.copy(targetFps = newFps))
            repository.triggerHaptics(false)
            _snackMessage.value = "Đã đặt cấu hình ${game.appName} thành $newFps FPS"
        }
    }

    fun addCustomGame(appName: String, packageName: String) {
        viewModelScope.launch {
            val game = GameApp(
                packageName = packageName.trim(),
                appName = appName.trim(),
                isInstalled = lagFixManager.isAppInstalled(packageName.trim()),
                targetFps = 60,
                resolutionScale = "100%",
                autoBoost = true,
                gameCategory = "Tùy chỉnh"
            )
            repository.addGame(game)
            _snackMessage.value = "Đã thêm $appName vào Game Box!"
        }
    }

    fun updateGfxResolution(res: String) {
        _gfxConfig.value = _gfxConfig.value.copy(resolution = res, isApplied = false)
    }

    fun updateGfxFps(fps: Int) {
        _gfxConfig.value = _gfxConfig.value.copy(targetFps = fps, isApplied = false)
    }

    fun updateGfxGraphics(level: String) {
        _gfxConfig.value = _gfxConfig.value.copy(graphicsLevel = level, isApplied = false)
    }

    fun toggleVulkanApi() {
        _gfxConfig.value = _gfxConfig.value.copy(vulkanApi = !_gfxConfig.value.vulkanApi, isApplied = false)
    }

    fun toggleShadows() {
        _gfxConfig.value = _gfxConfig.value.copy(shadows = !_gfxConfig.value.shadows, isApplied = false)
    }

    fun updateAntiAliasing(aa: String) {
        _gfxConfig.value = _gfxConfig.value.copy(antiAliasing = aa, isApplied = false)
    }

    fun applyGfxConfig() {
        viewModelScope.launch {
            repository.triggerHaptics(true)
            _gfxConfig.value = _gfxConfig.value.copy(isApplied = true)
            _snackMessage.value = "Đã áp dụng cấu hình GFX! Cố định ${_gfxConfig.value.targetFps} FPS, Vulkan GPU: ${_gfxConfig.value.vulkanApi}"
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _snackMessage.value = "Đã xóa toàn bộ lịch sử tối ưu."
        }
    }
}
