package com.example.game.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    onMove: (forward: Boolean, back: Boolean, left: Boolean, right: Boolean, isSprint: Boolean) -> Unit
) {
    var rawOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedOffset by animateOffsetAsState(
        targetValue = if (isDragging) rawOffset else Offset.Zero,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "joystick_spring"
    )

    val maxRadiusPx = remember(size) { 140f }

    Box(
        modifier = modifier
            .size(size)
            .testTag("virtual_joystick")
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33000000),
                        Color(0x660B051D),
                        Color(0x9905020E)
                    )
                ),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val delta = offset - center
                        val dist = delta.getDistance()
                        val clamped = if (dist > maxRadiusPx) {
                            val angle = atan2(delta.y, delta.x)
                            Offset(cos(angle) * maxRadiusPx, sin(angle) * maxRadiusPx)
                        } else delta
                        rawOffset = clamped

                        val normX = clamped.x / maxRadiusPx
                        val normY = clamped.y / maxRadiusPx
                        val mag = sqrt(normX * normX + normY * normY)
                        onMove(
                            normY < -0.28f,
                            normY > 0.28f,
                            normX < -0.28f,
                            normX > 0.28f,
                            mag > 0.88f
                        )
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = rawOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxRadiusPx) {
                            val angle = atan2(newOffset.y, newOffset.x)
                            Offset(cos(angle) * maxRadiusPx, sin(angle) * maxRadiusPx)
                        } else newOffset
                        rawOffset = clamped

                        val normX = clamped.x / maxRadiusPx
                        val normY = clamped.y / maxRadiusPx
                        val mag = sqrt(normX * normX + normY * normY)
                        onMove(
                            normY < -0.28f,
                            normY > 0.28f,
                            normX < -0.28f,
                            normX > 0.28f,
                            mag > 0.88f
                        )
                    },
                    onDragEnd = {
                        isDragging = false
                        rawOffset = Offset.Zero
                        onMove(false, false, false, false, false)
                    },
                    onDragCancel = {
                        isDragging = false
                        rawOffset = Offset.Zero
                        onMove(false, false, false, false, false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Outer guideline ring
            drawCircle(
                color = if (isDragging) Color(0x66FFC107) else Color(0x33FFFFFF),
                radius = maxRadiusPx,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Direction ticks (N, S, W, E)
            val tickLen = 8.dp.toPx()
            drawLine(
                color = Color(0x55FFFFFF),
                start = Offset(center.x, center.y - maxRadiusPx),
                end = Offset(center.x, center.y - maxRadiusPx + tickLen),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0x55FFFFFF),
                start = Offset(center.x, center.y + maxRadiusPx),
                end = Offset(center.x, center.y + maxRadiusPx - tickLen),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0x55FFFFFF),
                start = Offset(center.x - maxRadiusPx, center.y),
                end = Offset(center.x - maxRadiusPx + tickLen, center.y),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0x55FFFFFF),
                start = Offset(center.x + maxRadiusPx, center.y),
                end = Offset(center.x + maxRadiusPx - tickLen, center.y),
                strokeWidth = 2f
            )

            // Thumbstick center knob
            val knobCenter = center + animatedOffset
            val knobRadius = 26.dp.toPx()

            // Glow around knob when sprinting/dragging
            if (isDragging) {
                drawCircle(
                    color = Color(0x44FFB300),
                    radius = knobRadius + 10.dp.toPx(),
                    center = knobCenter
                )
            }

            // Outer knob rim
            drawCircle(
                color = Color(0xFFFFA000),
                radius = knobRadius,
                center = knobCenter
            )

            // Inner knob fill
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = knobRadius - 3.dp.toPx(),
                center = knobCenter
            )

            // Center accent dot
            drawCircle(
                color = Color(0xFF5D4037),
                radius = 6.dp.toPx(),
                center = knobCenter
            )
        }
    }
}
