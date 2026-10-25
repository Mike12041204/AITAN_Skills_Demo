package github.mike12041204.aitanskillsdemo.ui

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import github.mike12041204.aitanskillsdemo.data.Drone
import github.mike12041204.aitanskillsdemo.data.Entity
import github.mike12041204.aitanskillsdemo.data.FieldRepository
import github.mike12041204.aitanskillsdemo.data.Target

private const val BORDER_STROKE_WIDTH_CM = 0.02f
private const val BORDER_COLOR = 0xFF79747EL // Gray
private const val GRID_COLOR = 0xFFCAC4D0L   // Light gray

private const val PATH_STROKE_WIDTH_CM = 0.03f
private const val PATH_COLOR = 0xFF1E88E5L   // Blue
private const val PATH_DOT_LENGTH_CM = 0.01f
private const val PATH_DOT_GAP_CM = 0.1f

private const val DRONE_COLOR = 0xFF90CAF9L            // Light blue
private const val DRONE_SELECTED_COLOR = 0xFF1E88E5L   // Blue
private const val TARGET_COLOR = 0xFFFFCC80L           // Light orange
private const val TARGET_SELECTED_COLOR = 0xFFFB8C00L  // Orange

// Draws the field at real life size and lets the user tap entities and drag around
@Composable
fun FieldScene(modifier: Modifier = Modifier, viewModel: FieldViewModel = viewModel()) {
    val version by viewModel.version.subscribeAsState(0L)

    val pxPerCm = screenPxPerCm()
    val fieldSize = Size(
        FieldRepository.FIELD_WIDTH_CM * pxPerCm,
        FieldRepository.FIELD_HEIGHT_CM * pxPerCm
    )
    val pan = viewModel.panCm * pxPerCm

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Tap to select or unselect an entity
                detectTapGestures { tap ->
                    viewModel.onFieldTapped(fieldPxToCm(tap, viewModel.panCm * pxPerCm, pxPerCm))
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

        drawBorder(pan, fieldSize, pxPerCm)
        drawGrid(pan, fieldSize, pxPerCm)

        // Commands first so entities are drawn on top of them
        viewModel.entities.filterIsInstance<Drone>().forEach { drawDroneCommand(it, pan, pxPerCm) }
        viewModel.entities.forEach { drawEntity(it, pan, pxPerCm) }
    }
}

// Outline around the whole field
private fun DrawScope.drawBorder(pan: Offset, fieldSize: Size, pxPerCm: Float) {
    drawRect(
        Color(BORDER_COLOR),
        topLeft = pan,
        size = fieldSize,
        style = Stroke(BORDER_STROKE_WIDTH_CM * pxPerCm)
    )
}

// 1 cm grid, vertical lines then horizontal lines
private fun DrawScope.drawGrid(pan: Offset, fieldSize: Size, pxPerCm: Float) {
    for (i in 1..<FieldRepository.FIELD_WIDTH_CM.toInt()) {
        val x = pan.x + i * pxPerCm
        drawLine(Color(GRID_COLOR), Offset(x, pan.y), Offset(x, pan.y + fieldSize.height))
    }
    for (i in 1..<FieldRepository.FIELD_HEIGHT_CM.toInt()) {
        val y = pan.y + i * pxPerCm
        drawLine(Color(GRID_COLOR), Offset(pan.x, y), Offset(pan.x + fieldSize.width, y))
    }
}

// Shows the part of the drone's path it has not reached yet, the line style depends on the command
private fun DrawScope.drawDroneCommand(drone: Drone, pan: Offset, pxPerCm: Float) {
    when (drone.command) {
        // Solid line
        Drone.DroneCommand.STRAIGHT_ATTACK -> drawRemainingPath(
            drone,
            pan,
            pxPerCm,
            null
        )

        // Dotted line
        Drone.DroneCommand.EVASIVE_ATTACK -> drawRemainingPath(
            drone,
            pan,
            pxPerCm,
            PathEffect.dashPathEffect(
                floatArrayOf(PATH_DOT_LENGTH_CM * pxPerCm, PATH_DOT_GAP_CM * pxPerCm)
            )
        )

        // Solid loop
        Drone.DroneCommand.PATROL -> drawRemainingPath(
            drone,
            pan,
            pxPerCm,
            null
        )

        // Nothing to draw for the other commands, such as NONE
        else -> {}
    }
}

// Line from the drone through every point of its path it has not reached yet, pathEffect null for solid
private fun DrawScope.drawRemainingPath(
    drone: Drone,
    pan: Offset,
    pxPerCm: Float,
    pathEffect: PathEffect?
) {
    val dronePath = drone.path ?: return

    val start = fieldCmToPx(drone.positionCm.toOffset(), pan, pxPerCm)
    val line = Path()
    line.moveTo(start.x, start.y)

    for (point in dronePath.remainingPoints) {
        val next = fieldCmToPx(point.toOffset(), pan, pxPerCm)
        line.lineTo(next.x, next.y)
    }

    drawPath(
        line,
        Color(PATH_COLOR),
        style = Stroke(
            width = PATH_STROKE_WIDTH_CM * pxPerCm,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = pathEffect
        )
    )
}

// Draws each kind of entity in its own look
private fun DrawScope.drawEntity(entity: Entity, pan: Offset, pxPerCm: Float) {
    when (entity) {
        is Drone -> drawDrone(entity, pan, pxPerCm)
        is Target -> drawTarget(entity, pan, pxPerCm)
    }
}

// Circle, size is the diameter in cm
private fun DrawScope.drawDrone(drone: Drone, pan: Offset, pxPerCm: Float) {
    val color: Long

    if (drone.selected) {
        color = DRONE_SELECTED_COLOR
    } else {
        color = DRONE_COLOR
    }

    drawCircle(
        Color(color),
        radius = drone.sizeCm * pxPerCm / 2,
        center = fieldCmToPx(drone.positionCm.toOffset(), pan, pxPerCm)
    )
}

// Square, size is the side length in cm
private fun DrawScope.drawTarget(target: Target, pan: Offset, pxPerCm: Float) {
    val color: Long

    if (target.selected) {
        color = TARGET_SELECTED_COLOR
    } else {
        color = TARGET_COLOR
    }

    val sizePx = target.sizeCm * pxPerCm
    val center = fieldCmToPx(target.positionCm.toOffset(), pan, pxPerCm)

    // drawRect starts in the top left corner, so move back half the size from the center
    drawRect(
        Color(color),
        topLeft = center - Offset(sizePx / 2, sizePx / 2),
        size = Size(sizePx, sizePx)
    )
}
