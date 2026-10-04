package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.engine.BatteryTelemetry
import com.example.engine.DisplayCpuTelemetry
import com.example.engine.NetworkTelemetry
import com.example.engine.RamTelemetry
import com.example.engine.StorageTelemetry
import com.example.ui.components.GamerHudGauge
import com.example.ui.components.TelemetryCard
import com.example.ui.components.TurboBoostButton
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.FixLagTab
import com.example.viewmodel.FixLagViewModel

@Composable
fun DashboardScreen(
    viewModel: FixLagViewModel,
    modifier: Modifier = Modifier
) {
    val ram by viewModel.ramTelemetry.collectAsStateWithLifecycle()
    val storage by viewModel.storageTelemetry.collectAsStateWithLifecycle()
    val battery by viewModel.batteryTelemetry.collectAsStateWithLifecycle()
    val displayCpu by viewModel.displayCpuTelemetry.collectAsStateWithLifecycle()
    val network by viewModel.networkTelemetry.collectAsStateWithLifecycle()
    val boostProgress by viewModel.boostProgress.collectAsStateWithLifecycle()
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Header with Cyberpunk Gaming Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_turbo),
                    contentDescription = "Fix Lag Turbo Header",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    CyberBg.copy(alpha = 0.5f),
                                    CyberBg
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HỆ THỐNG SẴN SÀNG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Fix Lag Turbo Pro",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshTelemetry() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyberSurface.copy(alpha = 0.85f))
                            .border(1.dp, CyberCardBorder, CircleShape)
                            .testTag("refresh_telemetry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới",
                            tint = NeonCyan
                        )
                    }
                }
            }
        }

        // Active Booster Mode Pill
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceVariant)
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setTab(FixLagTab.BOOSTER) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(selectedMode.colorHex),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chế độ hiện tại: ${selectedMode.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Đổi >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // Main Speedometer & Turbo Boost Button Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GamerHudGauge(
                            valuePercent = ram.usedPercent,
                            label = "RAM ĐANG DÙNG",
                            subText = "${ram.usedMb} / ${ram.totalMb} MB",
                            size = 150.dp,
                            strokeWidth = 12.dp
                        )

                        GamerHudGauge(
                            valuePercent = storage.usedPercent,
                            label = "BỘ NHỚ MÁY",
                            subText = "${storage.freeGb} GB trống",
                            size = 150.dp,
                            strokeWidth = 12.dp,
                            customColor = NeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Turbo Boost Action Button
                    TurboBoostButton(
                        isBoosting = boostProgress.isBoosting,
                        progress = boostProgress.progressPercent,
                        onClick = { viewModel.startTurboBoost() },
                        size = 140.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (boostProgress.isBoosting) "HỆ THỐNG ĐANG TỐI ƯU CỰC ĐẠI…"
                        else "CHẠM ĐỂ FIX LAG & TĂNG TỐC TỨC THÌ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (boostProgress.isBoosting) TurboFlame else NeonCyan,
                        letterSpacing = 0.5.sp
                    )

                    // Step progress HUD indicator
                    AnimatedVisibility(
                        visible = boostProgress.isBoosting,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurfaceVariant)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = boostProgress.currentStepTitle,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { boostProgress.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = TurboFlame,
                                trackColor = Color(0xFF28354E)
                            )
                        }
                    }
                }
            }
        }

        // System Telemetry 2x2 Grid
        item {
            Text(
                text = "THÔNG SỐ THỜI GIAN THỰC",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelemetryCard(
                        icon = Icons.Default.Wifi,
                        title = "ĐỘ TRỄ MẠNG (PING)",
                        value = "${network.currentPingMs} ms",
                        statusText = network.packetQuality,
                        statusColor = if (network.currentPingMs < 40) NeonEmerald else WarningAmber,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTab(FixLagTab.NETWORK_PING) }
                    )

                    TelemetryCard(
                        icon = Icons.Default.BatteryChargingFull,
                        title = "NHIỆT ĐỘ PIN",
                        value = "${battery.tempCelsius}°C",
                        statusText = if (battery.tempCelsius < 38f) "Mát mẻ" else "Hơi ấm",
                        statusColor = if (battery.tempCelsius < 38f) NeonEmerald else WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelemetryCard(
                        icon = Icons.Default.Speed,
                        title = "TẦN SỐ QUÉT MÀN",
                        value = "${displayCpu.refreshRateHz} Hz",
                        statusText = "${displayCpu.screenResolution}",
                        statusColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )

                    TelemetryCard(
                        icon = Icons.Default.Memory,
                        title = "CPU ĐA NHÂN",
                        value = "${displayCpu.cpuCores} Nhân",
                        statusText = displayCpu.thermalThrottlingEstimate,
                        statusColor = NeonEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Action Shortcuts Bar
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CÔNG CỤ FIX LAG NHANH",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Game Box button
                QuickActionButton(
                    icon = Icons.Default.SportsEsports,
                    label = "Game Box",
                    color = TurboFlame,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(FixLagTab.GAME_BOX) }
                )

                // Cache Cleaner button
                QuickActionButton(
                    icon = Icons.Default.CleaningServices,
                    label = "Dọn Rác RAM",
                    color = NeonEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(FixLagTab.BOOSTER) }
                )

                // Network Ping button
                QuickActionButton(
                    icon = Icons.Default.Wifi,
                    label = "Test Ping",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(FixLagTab.NETWORK_PING) }
                )
            }
        }
    }

    // Success Dialog when boost completed
    if (boostProgress.isCompleted) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissBoostResult() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Fix Lag Hoàn Tất!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Hệ thống đã được tối ưu triệt để:",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Đã giải phóng: ${boostProgress.freedMemoryMb} MB RAM\n" +
                                "• Giảm tình trạng giật lag & rớt FPS\n" +
                                "• Xóa bộ nhớ đệm pipeline đồ họa\n" +
                                "• Khóa độ trễ mạng ở mức thấp nhất",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissBoostResult() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(text = "TUYỆT VỜI", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}
