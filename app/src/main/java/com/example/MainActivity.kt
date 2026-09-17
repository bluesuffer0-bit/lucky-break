package com.example

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GameBridge
import com.example.game.GameSettings
import com.example.game.GameWebView
import com.example.game.HapticFeedbackManager
import com.example.game.ui.GameControlsOverlay
import com.example.game.ui.dialogs.HelpDialog
import com.example.game.ui.dialogs.QuestsDialog
import com.example.game.ui.dialogs.SettingsDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var hapticManager: HapticFeedbackManager
    private lateinit var gameBridge: GameBridge
    private var webViewInstance: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        hapticManager = HapticFeedbackManager(this)
        gameBridge = GameBridge(
            hapticManager = hapticManager,
            onEngineLoaded = {
                runOnUiThread {
                    // Update initial settings on bridge
                    gameBridge.setSensitivity(1.4f)
                    gameBridge.setInvertY(false)
                }
            }
        )

        setContent {
            MyApplicationTheme {
                LuckyBreakApp(
                    bridge = gameBridge,
                    haptic = hapticManager,
                    onWebViewCreated = { webViewInstance = it },
                    onRestartWorld = {
                        webViewInstance?.reload()
                        Toast.makeText(this, "World reloading...", Toast.LENGTH_SHORT).show()
                    },
                    onUnstuck = {
                        gameBridge.unstuck()
                        hapticManager.playHit()
                        Toast.makeText(this, "Teleported upward!", Toast.LENGTH_SHORT).show()
                    },
                    onSaveGame = {
                        gameBridge.saveGame()
                        hapticManager.playJackpot()
                        Toast.makeText(this, "World saved successfully!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }
}

@Composable
fun LuckyBreakApp(
    bridge: GameBridge,
    haptic: HapticFeedbackManager,
    onWebViewCreated: (WebView) -> Unit,
    onRestartWorld: () -> Unit,
    onUnstuck: () -> Unit,
    onSaveGame: () -> Unit
) {
    val playerStats by bridge.playerStats.collectAsStateWithLifecycle()
    val isEngineReady by bridge.isEngineReady.collectAsStateWithLifecycle()

    var showQuestsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    var gameSettings by remember { mutableStateOf(GameSettings()) }

    LaunchedEffect(gameSettings) {
        haptic.enabled = gameSettings.hapticsEnabled
        bridge.setSensitivity(gameSettings.sensitivity)
        bridge.setInvertY(gameSettings.invertY)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("game_root")
    ) {
        // Full screen Hardware Accelerated WebGL Voxel Game View
        GameWebView(
            bridge = bridge,
            modifier = Modifier.fillMaxSize(),
            onWebViewCreated = onWebViewCreated
        )

        // Native Mobile Controls HUD Overlay
        GameControlsOverlay(
            playerStats = playerStats,
            bridge = bridge,
            haptic = haptic,
            onOpenQuests = { showQuestsDialog = true },
            onOpenSettings = { showSettingsDialog = true },
            onOpenHelp = { showHelpDialog = true },
            modifier = Modifier.fillMaxSize()
        )

        // Splash / Loading screen while voxel generator boots up
        AnimatedVisibility(
            visible = !isEngineReady,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoadingSplashView()
        }

        // Dialogs
        if (showQuestsDialog) {
            QuestsDialog(onDismiss = { showQuestsDialog = false })
        }

        if (showSettingsDialog) {
            SettingsDialog(
                settings = gameSettings,
                onSettingsChanged = { gameSettings = it },
                onUnstuck = onUnstuck,
                onSaveGame = onSaveGame,
                onRestartWorld = {
                    showSettingsDialog = false
                    onRestartWorld()
                },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showHelpDialog) {
            HelpDialog(onDismiss = { showHelpDialog = false })
        }
    }
}

@Composable
fun LoadingSplashView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF130924),
                        Color(0xFF22113D),
                        Color(0xFF0D0517)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Lucky Break",
                    tint = Color(0xFF3E2723),
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "LUCKY BREAK",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = Color(0xFFFFD54F)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Voxel Sandbox & Slot Machine Crafting",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xCCFFFFFF)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color(0xFFFFD54F),
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Rolling mystery loot tables...",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0x99FFFFFF)
                )
            )
        }
    }
}
