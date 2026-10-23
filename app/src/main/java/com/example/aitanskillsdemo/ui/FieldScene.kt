package com.example.aitanskillsdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.dp
import com.example.aitanskillsdemo.data.Entity
import com.example.aitanskillsdemo.data.FieldRepository

const val SELECTION_TAP_DISTANCE = 40f

@Composable
fun FieldScene(modifier: Modifier = Modifier) {
    val repo = remember { FieldRepository() }
    val version by repo.version.subscribeAsState(0L)

    val axisColor = MaterialTheme.colorScheme.onSurface
    val circleColor = MaterialTheme.colorScheme.primary
    val selectedColor = Color(0xFFFFA500)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
            .pointerInput(repo.entities) {
                detectTapGestures { tap ->
                    repo.entities.firstOrNull { (it.center(size.toSize()) - tap).getDistance() < SELECTION_TAP_DISTANCE }?.let { entity ->
                        repo.sendSelection(entity)
                    }
                }
            }
    ) {
        // read version here causing canvas to redraw every field update
        version

        // Axes along the left and bottom edges
        drawLine(axisColor, Offset(0f, 0f), Offset(0f, size.height), strokeWidth = 3f)
        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 3f)

        repo.entities.forEach {
            drawCircle(if (it.selected) selectedColor else circleColor, radius = 10f, center = it.center(size))
        }
    }
}

// Entity position scaled to canvas pixels, y flipped so it grows upward
private fun Entity.center(size: Size) = Offset(
    x = xPosition / Entity.MAX_X * size.width,
    y = size.height - yPosition / Entity.MAX_Y * size.height
)
