package com.example.game.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.game.GameSettings

@Composable
fun SettingsDialog(
    settings: GameSettings,
    onSettingsChanged: (GameSettings) -> Unit,
    onUnstuck: () -> Unit,
    onSaveGame: () -> Unit,
    onRestartWorld: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("settings_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1B1429),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ Game Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sensitivity
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF291E3D))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Camera Touch Sensitivity",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = String.format("%.1fx", settings.sensitivity),
                                color = Color(0xFFFFD54F),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Slider(
                            value = settings.sensitivity,
                            onValueChange = { onSettingsChanged(settings.copy(sensitivity = it)) },
                            valueRange = 0.5f..3.0f,
                            steps = 25,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD54F),
                                activeTrackColor = Color(0xFFFFA000),
                                inactiveTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("sensitivity_slider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Invert Y
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF291E3D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Invert Camera Y-Axis",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Inverts up/down camera swipe direction",
                                color = Color(0xAAFFFFFF),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = settings.invertY,
                            onCheckedChange = { onSettingsChanged(settings.copy(invertY = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFD54F),
                                checkedTrackColor = Color(0xFFFFA000)
                            ),
                            modifier = Modifier.testTag("invert_y_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Haptics
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF291E3D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column {
                                Text(
                                    text = "Tactile Vibration (Haptics)",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Vibrates on block breaks, hits & jackpot",
                                    color = Color(0xAAFFFFFF),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Switch(
                            checked = settings.hapticsEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(hapticsEnabled = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFD54F),
                                checkedTrackColor = Color(0xFFFFA000)
                            ),
                            modifier = Modifier.testTag("haptics_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Unstuck & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onUnstuck,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("unstuck_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFFB74D)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unstuck Me")
                    }

                    Button(
                        onClick = onSaveGame,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_game_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C4DFF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save World")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRestartWorld,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restart_world_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reload / Fresh World")
                }
            }
        }
    }
}
