package com.example.game.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun HelpDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("help_dialog"),
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
                    Column {
                        Text(
                            text = "🎮 How to Play",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        )
                        Text(
                            text = "Lucky Break Survival Guide",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xBBFFFFFF)
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HelpSection(
                    icon = "🕹️",
                    title = "Mobile Controls",
                    body = "• Left Joystick: Drag to walk in any direction. Push outward to sprint!\n• Right Screen: Swipe anywhere to look around seamlessly.\n• ⛏️ Mine Button: Hold to dig blocks or attack enemies.\n• 🧱 Place Button: Place held item, eat food, or interact.\n• ⬆️ Jump & 🛡️ Sneak: Scale terrain or sneak along dangerous edges."
                )

                Spacer(modifier = Modifier.height(10.dp))

                HelpSection(
                    icon = "🎲",
                    title = "Random Drops & Rules",
                    body = "Every block type in Lucky Break has unpredictable randomized drops! Dirt could yield laser carrots, stone might drop TNT or diamonds, and leaves might drop mystery eggs. Track what you've found in your Codex (📖)!"
                )

                Spacer(modifier = Modifier.height(10.dp))

                HelpSection(
                    icon = "🎰",
                    title = "The 9-Item Gamble Recipe",
                    body = "Crafting in this world is a slot machine! In your inventory, combine nine miscellaneous items for a rolled prize. You might get junk... or a jackpot weapon with Thunder Shockwaves, Midas Touch, or Explosive Instability!"
                )

                Spacer(modifier = Modifier.height(10.dp))

                HelpSection(
                    icon = "🚗",
                    title = "Rollywagons & Creatures",
                    body = "Find or craft a Rollywagon and tap the 🚗 Drive button to zoom across the voxel landscape! Watch out for wild voxel spiders, crabs that swap items, and slimes."
                )
            }
        }
    }
}

@Composable
private fun HelpSection(icon: String, title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF291E3D))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xEEFFFFFF),
                    lineHeight = 18.sp
                )
            )
        }
    }
}
