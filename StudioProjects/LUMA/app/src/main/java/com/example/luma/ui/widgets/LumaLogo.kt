package com.example.luma.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.luma.ui.theme.LumaLightBlue
import com.example.luma.ui.theme.LumaSoftLavender
import com.example.luma.ui.theme.LumaWarmCream

@Composable
fun SleepingCatMoonLogo(size: Dp = 36.dp) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasWidth = size.toPx()
            val canvasHeight = size.toPx()

            // Background circle
            drawCircle(
                color = Color(0xFF161F38),
                radius = canvasWidth / 2f
            )

            // Crescent Moon
            val moonPath = Path().apply {
                addOval(Rect(canvasWidth * 0.2f, canvasWidth * 0.15f, canvasWidth * 0.85f, canvasWidth * 0.85f))
            }
            val cutoutPath = Path().apply {
                addOval(Rect(canvasWidth * 0.35f, canvasWidth * 0.1f, canvasWidth * 0.9f, canvasWidth * 0.8f))
            }
            val crescentPath = Path().apply {
                op(moonPath, cutoutPath, PathOperation.Difference)
            }
            drawPath(crescentPath, color = LumaWarmCream, style = Fill)

            // Sleeping Cat Body curled on the moon
            val catBodyPath = Path().apply {
                moveTo(canvasWidth * 0.35f, canvasHeight * 0.55f)
                quadraticTo(canvasWidth * 0.5f, canvasHeight * 0.45f, canvasWidth * 0.65f, canvasWidth * 0.5f)
                quadraticTo(canvasWidth * 0.75f, canvasHeight * 0.65f, canvasWidth * 0.6f, canvasHeight * 0.75f)
                quadraticTo(canvasWidth * 0.45f, canvasHeight * 0.8f, canvasWidth * 0.35f, canvasHeight * 0.55f)
                close()
            }
            drawPath(catBodyPath, color = Color(0xFF101426), style = Fill)

            // Cat Head
            drawCircle(
                color = Color(0xFF101426),
                radius = canvasWidth * 0.12f,
                center = Offset(canvasWidth * 0.38f, canvasHeight * 0.48f)
            )

            // Sparkle Stars
            drawCircle(color = LumaSoftLavender, radius = canvasWidth * 0.05f, center = Offset(canvasWidth * 0.75f, canvasHeight * 0.25f))
            drawCircle(color = LumaLightBlue, radius = canvasWidth * 0.035f, center = Offset(canvasWidth * 0.2f, canvasHeight * 0.65f))
            drawCircle(color = LumaWarmCream, radius = canvasWidth * 0.04f, center = Offset(canvasWidth * 0.7f, canvasHeight * 0.35f))
        }
    }
}
