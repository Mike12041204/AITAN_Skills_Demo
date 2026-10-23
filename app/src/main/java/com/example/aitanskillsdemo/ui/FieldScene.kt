package com.example.aitanskillsdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.aitanskillsdemo.data.Entity
import com.example.aitanskillsdemo.data.FieldRepository
import androidx.compose.ui.platform.LocalResources

const val SELECTION_TAP_DISTANCE = 40f

@Composable
fun FieldScene(modifier: Modifier = Modifier) {
    val repo = remember { FieldRepository() }
    val version by repo.version.subscribeAsState(0L)

    var pan by remember { mutableStateOf(Offset.Zero) }

    val axisColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    // screen pixels per real life centimeter
    val pxPerCm = LocalResources.current.displayMetrics.xdpi / 2.54f
    val fieldSize = Size(FieldRepository.FIELD_WIDTH * pxPerCm, FieldRepository.FIELD_HEIGHT * pxPerCm)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(repo.entities) {
                detectTapGestures { tap ->
                    repo.entities.firstOrNull { (it.center(pan, pxPerCm) - tap).getDistance() < SELECTION_TAP_DISTANCE }?.let { entity ->
                        repo.sendSelection(entity)
                    }
                }
            }
            .pointerInput(Unit) {
                // drag to move around the field, stopping at the field edges
                detectDragGestures { _, drag ->
                    pan = Offset(
                        (pan.x + drag.x).coerceIn(minOf(0f, size.width - fieldSize.width), 0f),
                        (pan.y + drag.y).coerceIn(minOf(0f, size.height - fieldSize.height), 0f)
                    )
                }
            }
    ) {
        // read version here causing canvas to redraw every field update
        version

        // 1 cm grid
        for (i in 1..<FieldRepository.FIELD_WIDTH.toInt()) {
            drawLine(gridColor, Offset(pan.x + i * pxPerCm, pan.y), Offset(pan.x + i * pxPerCm, pan.y + fieldSize.height))
        }
        for (i in 1..<FieldRepository.FIELD_HEIGHT.toInt()) {
            drawLine(gridColor, Offset(pan.x, pan.y + i * pxPerCm), Offset(pan.x + fieldSize.width, pan.y + i * pxPerCm))
        }

        // Field border
        drawRect(axisColor, topLeft = pan, size = fieldSize, style = Stroke(3f))

        repo.entities.forEach {
            drawCircle(Color(it.color), radius = 10f, center = it.center(pan, pxPerCm))
        }
    }
}

// Entity position in centimeters to screen pixels, y flipped so it grows upward
private fun Entity.center(pan: Offset, pxPerCm: Float) = Offset(
    x = pan.x + xPosition * pxPerCm,
    y = pan.y + (FieldRepository.FIELD_HEIGHT - yPosition) * pxPerCm
)