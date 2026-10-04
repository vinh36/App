package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.viewmodel.FixLagViewModel

@Composable
fun GfxToolScreen(
    viewModel: FixLagViewModel,
    modifier: Modifier = Modifier
) {
    val gfxConfig by viewModel.gfxConfig.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "CÔNG CỤ ĐỒ HỌA & FPS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonEmerald,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "GFX Tool Tối Ưu FPS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Tùy biến độ phân giải, mở khóa 90/120 FPS và khử giật hình",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Active GFX Status Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (gfxConfig.isApplied) NeonEmerald.copy(alpha = 0.15f) else CyberSurfaceVariant)
                    .border(
                        1.dp,
                        if (gfxConfig.isApplied) NeonEmerald else CyberCardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (gfxConfig.isApplied) Icons.Default.CheckCircle else Icons.Default.Tune,
                        contentDescription = null,
                        tint = if (gfxConfig.isApplied) NeonEmerald else NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (gfxConfig.isApplied) "Cấu hình GFX Đang Hoạt Động" else "Cấu hình chưa áp dụng",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${gfxConfig.resolution} • ${gfxConfig.targetFps} FPS • ${gfxConfig.graphicsLevel}",
                            fontSize = 11.sp,
                            color = if (gfxConfig.isApplied) NeonEmerald else TextMuted
                        )
                    }
                }
            }
        }

        // Resolution Selector
        item {
            GfxSectionCard(title = "ĐỘ PHÂN GIẢI MÀN HÌNH") {
                val resolutions = listOf("540p (Siêu mượt)", "720p (HD Chuẩn)", "1080p (FHD)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    resolutions.forEach { res ->
                        val isSelected = gfxConfig.resolution == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan else CyberSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else CyberCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateGfxResolution(res) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.substringBefore(" "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF090D16) else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Target FPS Selector
        item {
            GfxSectionCard(title = "MỤC TIÊU KHUNG HÌNH (FPS)") {
                val fpsList = listOf(30, 60, 90, 120)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fpsList.forEach { fps ->
                        val isSelected = gfxConfig.targetFps == fps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TurboFlame else CyberSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) TurboFlame else CyberCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateGfxFps(fps) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$fps",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                                Text(
                                    text = "FPS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Graphics Detail Level
        item {
            GfxSectionCard(title = "CHẤT LƯỢNG ĐỒ HỌA") {
                val levels = listOf("Siêu mượt (Smooth)", "Cân bằng (Balanced)", "HD Sắc nét", "HDR")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        val isSelected = gfxConfig.graphicsLevel == lvl
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyberSurfaceVariant else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else CyberCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateGfxGraphics(lvl) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lvl,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonCyan else TextPrimary
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF090D16),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Advanced Toggles (Vulkan GPU & Shadows)
        item {
            GfxSectionCard(title = "TÙY CHỌN NÂNG CAO") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Vulkan API
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Tăng Tốc Vulkan GPU", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Giảm tải CPU lên đến 30% khi render đồ họa 3D", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = gfxConfig.vulkanApi,
                            onCheckedChange = { viewModel.toggleVulkanApi() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonEmerald,
                                checkedTrackColor = NeonEmerald.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberBg
                            )
                        )
                    }

                    // Shadows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Hiệu Ứng Đổ Bóng (Shadows)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Khuyên tắt để tăng từ 15-20 FPS ổn định", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = gfxConfig.shadows,
                            onCheckedChange = { viewModel.toggleShadows() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WarningAmber,
                                checkedTrackColor = WarningAmber.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberBg
                            )
                        )
                    }
                }
            }
        }

        // Apply Button
        item {
            Button(
                onClick = { viewModel.applyGfxConfig() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_gfx_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.DisplaySettings,
                    contentDescription = null,
                    tint = Color(0xFF090D16),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ÁP DỤNG CẤU HÌNH GFX NGAY",
                    color = Color(0xFF090D16),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun GfxSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
