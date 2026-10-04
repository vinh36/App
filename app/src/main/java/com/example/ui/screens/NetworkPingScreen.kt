package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.engine.PingNode
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
fun NetworkPingScreen(
    viewModel: FixLagViewModel,
    modifier: Modifier = Modifier
) {
    val network by viewModel.networkTelemetry.collectAsStateWithLifecycle()
    val pingNodes by viewModel.pingNodes.collectAsStateWithLifecycle()
    val isAnyTesting = pingNodes.any { it.isTesting }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = "BỘ ĐO & ỔN ĐỊNH PING MẠNG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Giảm Ping & Chống Giật Mạng",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Kiểm tra độ trễ thực tế đến các gateway và máy chủ game",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Connection status card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = network.connectionType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Trạng thái: ${network.packetQuality}",
                            fontSize = 12.sp,
                            color = NeonEmerald
                        )
                        Text(
                            text = "IP Cục bộ: ${network.ipAddress}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${network.currentPingMs}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (network.currentPingMs < 40) NeonEmerald else WarningAmber
                        )
                        Text(
                            text = "ms (Độ trễ)",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Button: Test All Nodes
        item {
            Button(
                onClick = { viewModel.testAllPingNodes() },
                enabled = !isAnyTesting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_all_ping_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                if (isAnyTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF090D16),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ĐANG ĐO ĐỘ TRỄ MÁY CHỦ…", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "KIỂM TRA PING TOÀN BỘ SERVER", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Ping Nodes List Header
        item {
            Text(
                text = "DANH SÁCH MÁY CHỦ GATEWAY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        // Ping nodes items
        items(pingNodes, key = { it.id }) { node ->
            PingNodeItem(node = node)
        }

        // DNS Recommendations Card
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Khuyến Nghị DNS Gaming Chống Nghẽn",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Cloudflare 1.1.1.1 (Tối ưu phản hồi nhanh nhất)\n" +
                                "   • DNS chính: 1.1.1.1 | Phụ: 1.0.0.1\n\n" +
                                "2. Google Public DNS (Ổn định xuyên quốc tế)\n" +
                                "   • DNS chính: 8.8.8.8 | Phụ: 8.8.4.4",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // Pro Tips for Gaming Network
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Mẹo Chống Nhảy Ping Khi Leo Rank",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Ưu tiên kết nối Wi-Fi 5GHz thay vì 2.4GHz để loại bỏ nhiễu sóng.\n" +
                                "• Tạm dừng tự động đồng bộ Google Photos, Drive và tải bản cập nhật ngầm.\n" +
                                "• Giữ khoảng cách gần với Router hoặc bật tính năng Tăng tốc dữ liệu kép (Wi-Fi + 4G).",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PingNodeItem(node: PingNode) {
    val pingColor = when {
        node.pingMs == 0 -> TextMuted
        node.pingMs < 30 -> NeonEmerald
        node.pingMs < 75 -> WarningAmber
        else -> TurboFlame
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${node.location} • ${node.host}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            if (node.isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = NeonCyan,
                    strokeWidth = 2.dp
                )
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (node.pingMs > 0) "${node.pingMs} ms" else "--",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = pingColor
                    )
                    Text(
                        text = node.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = pingColor
                    )
                }
            }
        }
    }
}
