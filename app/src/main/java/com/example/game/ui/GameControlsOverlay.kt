package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameBridge
import com.example.game.HapticFeedbackManager
import com.example.game.PlayerStats

@Composable
fun GameControlsOverlay(
    playerStats: PlayerStats,
    bridge: GameBridge,
    haptic: HapticFeedbackManager,
    onOpenQuests: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSneaking by remember { mutableStateOf(false) }
    var isSprinting by remember { mutableStateOf(false) }
    var activeHotbarSlot by remember { mutableStateOf(1) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // TOP STATUS BAR & QUICK ACTIONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Stats: HP, Position
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xCC130D24),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFD54F))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Health
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${playerStats.hp}/${playerStats.maxHp}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Coordinates
                    Text(
                        text = "XYZ: ${playerStats.x}, ${playerStats.y}, ${playerStats.z}",
                        color = Color(0xBBFFFFFF),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Quick Actions: Inventory, Quests, Codex, Drive, Drop, Help, Settings
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Inventory (E)
                ActionButtonSmall(
                    icon = Icons.Default.Work,
                    label = "INV",
                    testTag = "btn_inventory",
                    onClick = {
                        haptic.playLightTap()
                        bridge.openInventory()
                    }
                )

                // Quests (J)
                ActionButtonSmall(
                    icon = Icons.Default.PlayArrow,
                    label = "QUESTS",
                    testTag = "btn_quests",
                    onClick = {
                        haptic.playLightTap()
                        onOpenQuests()
                    }
                )

                // Codex (K)
                ActionButtonSmall(
                    icon = Icons.Default.MenuBook,
                    label = "CODEX",
                    testTag = "btn_codex",
                    onClick = {
                        haptic.playLightTap()
                        bridge.openCodex()
                    }
                )

                // Wagon (F)
                ActionButtonSmall(
                    icon = Icons.Default.DirectionsCar,
                    label = "RIDE",
                    testTag = "btn_wagon",
                    onClick = {
                        haptic.playLightTap()
                        bridge.interactWagon()
                    }
                )

                // Drop Item (Q)
                ActionButtonSmall(
                    icon = Icons.Default.Delete,
                    label = "DROP",
                    testTag = "btn_drop",
                    onClick = {
                        haptic.playLightTap()
                        bridge.dropItem(false)
                    }
                )

                // Help Guide
                ActionButtonSmall(
                    icon = Icons.AutoMirrored.Filled.Help,
                    label = "HELP",
                    testTag = "btn_help",
                    onClick = {
                        haptic.playLightTap()
                        onOpenHelp()
                    }
                )

                // Settings
                ActionButtonSmall(
                    icon = Icons.Default.Settings,
                    label = "SET",
                    testTag = "btn_settings",
                    onClick = {
                        haptic.playLightTap()
                        onOpenSettings()
                    }
                )
            }
        }

        // BOTTOM LEFT: Virtual Joystick for Movement
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 48.dp)
        ) {
            VirtualJoystick(
                size = 150.dp,
                onMove = { f, b, l, r, sprint ->
                    bridge.setMovement(f, b, l, r)
                    if (sprint && !isSprinting) {
                        bridge.setSprint(true)
                    } else if (!sprint && !isSprinting) {
                        bridge.setSprint(false)
                    }
                }
            )
        }

        // BOTTOM CENTER: Hotbar Selector (Slots 1 to 9)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .testTag("hotbar_bar"),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD120B20),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFD54F))
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (slot in 1..9) {
                    val isSelected = activeHotbarSlot == slot
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color(0x66FFC107) else Color(0x33FFFFFF)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFFD54F) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                activeHotbarSlot = slot
                                haptic.playLightTap()
                                bridge.selectHotbar(slot)
                            }
                            .testTag("hotbar_slot_$slot"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$slot",
                            color = if (isSelected) Color(0xFFFFD54F) else Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        // BOTTOM RIGHT: Action Buttons (Mine, Place, Jump, Sneak, Sprint)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 48.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary row: Sneak & Sprint
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sprint Toggle
                    ActionCircularButton(
                        size = 46.dp,
                        backgroundColor = if (isSprinting) Color(0xFFFF6F00) else Color(0x992E1B4E),
                        icon = Icons.Default.FlashOn,
                        contentDescription = "Sprint",
                        testTag = "btn_sprint_toggle",
                        onClick = {
                            isSprinting = !isSprinting
                            haptic.playLightTap()
                            bridge.setSprint(isSprinting)
                        }
                    )

                    // Sneak Toggle
                    ActionCircularButton(
                        size = 46.dp,
                        backgroundColor = if (isSneaking) Color(0xFF00897B) else Color(0x992E1B4E),
                        icon = Icons.Default.Security,
                        contentDescription = "Sneak",
                        testTag = "btn_sneak_toggle",
                        onClick = {
                            isSneaking = !isSneaking
                            haptic.playLightTap()
                            bridge.setSneak(isSneaking)
                        }
                    )

                    // Jump Button
                    TouchHoldButton(
                        size = 54.dp,
                        backgroundColor = Color(0xCC3949AB),
                        icon = Icons.Default.ArrowUpward,
                        contentDescription = "Jump",
                        testTag = "btn_jump",
                        onDown = {
                            haptic.playLightTap()
                            bridge.setJump(true)
                        },
                        onUp = {
                            bridge.setJump(false)
                        }
                    )
                }

                // Primary row: Mine/Attack and Place/Use
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Place / Use Button (Right Click)
                    TouchHoldButton(
                        size = 62.dp,
                        backgroundColor = Color(0xDD00897B),
                        icon = Icons.Default.PanTool,
                        contentDescription = "Place or Interact",
                        testTag = "btn_place",
                        onDown = {
                            haptic.playLightTap()
                            bridge.setPlace(true)
                        },
                        onUp = {
                            bridge.setPlace(false)
                        }
                    )

                    // Mine / Attack Button (Left Click) - Biggest Button
                    TouchHoldButton(
                        size = 76.dp,
                        backgroundColor = Color(0xEEFF8F00),
                        icon = Icons.Default.FlashOn,
                        label = "MINE",
                        contentDescription = "Mine or Attack",
                        testTag = "btn_mine",
                        onDown = {
                            haptic.playBlockBreak()
                            bridge.setMine(true)
                        },
                        onUp = {
                            bridge.setMine(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonSmall(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xCC201335),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFD54F))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )
        }
    }
}

@Composable
private fun ActionCircularButton(
    size: androidx.compose.ui.unit.Dp,
    backgroundColor: Color,
    icon: ImageVector,
    contentDescription: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

@Composable
private fun TouchHoldButton(
    size: androidx.compose.ui.unit.Dp,
    backgroundColor: Color,
    icon: ImageVector,
    label: String? = null,
    contentDescription: String,
    testTag: String,
    onDown: () -> Unit,
    onUp: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (isPressed) Color.White.copy(alpha = 0.35f) else backgroundColor
            )
            .border(
                width = if (isPressed) 2.5.dp else 1.5.dp,
                color = if (isPressed) Color(0xFFFFD54F) else Color(0x88FFFFFF),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onDown()
                        val released = tryAwaitRelease()
                        isPressed = false
                        onUp()
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isPressed) Color(0xFFFFD54F) else Color.White,
                modifier = Modifier.size(if (label != null) size * 0.38f else size * 0.54f)
            )
            if (label != null) {
                Text(
                    text = label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
