package com.example.aitanskillsdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aitanskillsdemo.data.Entity
import com.example.aitanskillsdemo.data.FieldRepository

private const val BORDER_STROKE_WIDTH = 3f
private const val BORDER_COLOR = 0xFF79747EL // Gray
private const val GRID_COLOR = 0xFFCAC4D0L   // Light gray

@Composable
fun FieldScene(modifier: Modifier = Modifier, viewModel: FieldViewModel = viewModel()) {
    val version by viewModel.version.subscribeAsState(0L)

    val pxPerCm = screenPxPerCm()
    val fieldSize = Size(FieldRepository.FIELD_WIDTH * pxPerCm, FieldRepository.FIELD_HEIGHT * pxPerCm)
    val pan = viewModel.panCm * pxPerCm

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    viewModel.onFieldTapped(screenToField(tap, viewModel.panCm * pxPerCm, pxPerCm))
                }
            }
            .pointerInput(Unit) {
                // Drag to move around the field, stopping at the field edges
                detectDragGestures { _, drag ->
                    val newPan = viewModel.panCm * pxPerCm + drag
                    viewModel.panCm = Offset(
                        newPan.x.coerceIn(minOf(0f, size.width - fieldSize.width), 0f),
                        newPan.y.coerceIn(minOf(0f, size.height - fieldSize.height), 0f)
                    ) / pxPerCm
                }
            }
    ) {
        // Read version here causing canvas to redraw every field update
        version

        // 1 cm grid
        for (i in 1..<FieldRepository.FIELD_WIDTH.toInt()) {
            val x = pan.x + i * pxPerCm
            drawLine(Color(GRID_COLOR), Offset(x, pan.y), Offset(x, pan.y + fieldSize.height))
        }
        for (i in 1..<FieldRepository.FIELD_HEIGHT.toInt()) {
            val y = pan.y + i * pxPerCm
            drawLine(Color(GRID_COLOR), Offset(pan.x, y), Offset(pan.x + fieldSize.width, y))
        }

        // Field border
        drawRect(Color(BORDER_COLOR), topLeft = pan, size = fieldSize, style = Stroke(BORDER_STROKE_WIDTH))

        // Entity size is its diameter or side length in cm
        viewModel.entities.forEach {
            val sizePx = it.size * pxPerCm
            val position = entityScreenPosition(it, pan, pxPerCm)
            when (it.shape) {
                Entity.EntityShape.CIRCLE -> drawCircle(Color(it.color), radius = sizePx / 2, center = position)
                Entity.EntityShape.SQUARE -> drawRect(Color(it.color), topLeft = position - Offset(sizePx / 2, sizePx / 2), size = Size(sizePx, sizePx))
            }
        }
    }
}
