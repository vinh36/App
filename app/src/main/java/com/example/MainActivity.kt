package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BoosterScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GameBoxScreen
import com.example.ui.screens.GfxToolScreen
import com.example.ui.screens.NetworkPingScreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.viewmodel.FixLagTab
import com.example.viewmodel.FixLagViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FixLagViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FixLagApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val tab: FixLagTab,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun FixLagApp(viewModel: FixLagViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Back handling: if not on Dashboard, return to Dashboard
    if (currentTab != FixLagTab.DASHBOARD) {
        BackHandler {
            viewModel.setTab(FixLagTab.DASHBOARD)
        }
    }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackMessage()
        }
    }

    val navItems = listOf(
        NavItem(FixLagTab.DASHBOARD, "Tổng quan", Icons.Default.Home, "nav_dashboard"),
        NavItem(FixLagTab.BOOSTER, "Tăng tốc", Icons.Default.Bolt, "nav_booster"),
        NavItem(FixLagTab.GAME_BOX, "Game Box", Icons.Default.SportsEsports, "nav_gamebox"),
        NavItem(FixLagTab.NETWORK_PING, "Ping Mạng", Icons.Default.Wifi, "nav_network"),
        NavItem(FixLagTab.GFX_TOOL, "GFX Tool", Icons.Default.DisplaySettings, "nav_gfxtool")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBg),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(width = 1.dp, color = CyberCardBorder),
                containerColor = CyberSurface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 250),
                label = "screen_crossfade"
            ) { tab ->
                when (tab) {
                    FixLagTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    FixLagTab.BOOSTER -> BoosterScreen(viewModel = viewModel)
                    FixLagTab.GAME_BOX -> GameBoxScreen(viewModel = viewModel)
                    FixLagTab.NETWORK_PING -> NetworkPingScreen(viewModel = viewModel)
                    FixLagTab.GFX_TOOL -> GfxToolScreen(viewModel = viewModel)
                }
            }
        }
    }
}
