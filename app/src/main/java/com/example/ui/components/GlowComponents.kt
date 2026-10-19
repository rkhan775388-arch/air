package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextPrimary

@Composable
fun AirNineGlowOrb(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    soundLevel: Float = 0.5f,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isActive) 800 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.4f else 0.15f,
        targetValue = if (isActive) 0.9f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isActive) 800 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 1.5f)
            .clickable(onClick = onClick)
    ) {
        // Outer glow canvas
        Canvas(modifier = Modifier.size(size * 1.5f)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2.6f) * (if (isActive) (pulseScale + soundLevel * 0.3f) else 1f)

            // Outer blur gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanAccent.copy(alpha = glowAlpha),
                        ElectricBlue.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.4f
                ),
                radius = radius * 1.4f,
                center = center
            )

            // Outer ring
            drawCircle(
                color = if (isActive) CyanAccent else ElectricBlue.copy(alpha = 0.6f),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }

        // Inner solid core
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = if (isActive) listOf(CyanAccent, ElectricBlueBright, NeonTeal)
                        else listOf(ElectricBlue, NavySurface)
                    )
                )
                .border(2.dp, CyanAccent.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "9",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = if (isActive) NavyDarkBackground else TextPrimary
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isListening: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveAnim"
    )

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val factor = if (isListening) {
                val offset = (i.toFloat() / barCount)
                val dynamic = kotlin.math.sin((animProgress + offset) * Math.PI.toFloat()).coerceIn(0.15f, 1f)
                dynamic
            } else {
                0.2f
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp * factor)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CyanAccent, ElectricBlue)
                        )
                    )
            )
        }
    }
}

@Composable
fun ModelBadgePill(
    modelName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCard)
            .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(CyanAccent)
        )
        Text(
            text = modelName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NavyBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyCard)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    } else {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyCard)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
    }

    Box(modifier = cardModifier) {
        content()
    }
}
