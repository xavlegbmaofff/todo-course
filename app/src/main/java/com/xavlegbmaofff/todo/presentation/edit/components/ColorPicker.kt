package com.xavlegbmaofff.todo.presentation.edit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    initialColor: Color = Color.Red,
    onColorSelected: (Color) -> Unit
) {
    val hsv = remember { colorToHsv(initialColor) }

    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var brightness by remember { mutableFloatStateOf(hsv[2]) }

    val currentColor = remember(hue, saturation, brightness) {
        Color.hsv(hue, saturation, brightness)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(currentColor)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Яркость: ${(brightness * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )

        Slider(
            value = brightness,
            onValueChange = {
                brightness = it
                onColorSelected(Color.hsv(hue, saturation, brightness))
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Палитра цветов",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ColorPalette(
            hue = hue,
            saturation = saturation,
            brightness = brightness,
            onColorChanged = { newHue, newSaturation ->
                hue = newHue
                saturation = newSaturation
                onColorSelected(Color.hsv(hue, saturation, brightness))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
fun ColorPalette(
    hue: Float,
    saturation: Float,
    brightness: Float,
    onColorChanged: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newHue = (offset.x / size.x).coerceIn(0f, 1f) * 360f
                        val newSaturation = 1f - (offset.y / size.y).coerceIn(0f, 1f)
                        onColorChanged(newHue, newSaturation)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newHue = (change.position.x / size.x).coerceIn(0f, 1f) * 360f
                        val newSaturation = 1f - (change.position.y / size.y).coerceIn(0f, 1f)
                        onColorChanged(newHue, newSaturation)
                    }
                }
        ) {
            size = Offset(this.size.width, this.size.height)

            val hueColors = (0..360 step 30).map { h ->
                Color.hsv(h.toFloat(), 1f, brightness)
            }

            drawRect(
                brush = Brush.horizontalGradient(hueColors)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 1f),
                        Color.White.copy(alpha = 0f)
                    )
                )
            )

            val x = (hue / 360f) * this.size.width
            val y = (1f - saturation) * this.size.height

            val crosshairColor = if (brightness > 0.5f) Color.Black else Color.White

            drawCircle(
                color = crosshairColor,
                radius = 16.dp.toPx(),
                center = Offset(x, y),
                style = Stroke(width = 3.dp.toPx())
            )

            drawCircle(
                color = Color.hsv(hue, saturation, brightness),
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )

            drawCircle(
                color = crosshairColor,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private fun colorToHsv(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val v = max
    val s = if (max == 0f) 0f else delta / max

    val h = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6)
        max == g -> 60f * (((b - r) / delta) + 2)
        else -> 60f * (((r - g) / delta) + 4)
    }

    return floatArrayOf(
        if (h < 0) h + 360 else h,
        s,
        v
    )
}
