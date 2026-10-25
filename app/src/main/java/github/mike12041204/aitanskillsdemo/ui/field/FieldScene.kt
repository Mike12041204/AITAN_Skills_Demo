package github.mike12041204.aitanskillsdemo.ui.field

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import github.mike12041204.aitanskillsdemo.R
import github.mike12041204.aitanskillsdemo.data.entities.DroneView
import github.mike12041204.aitanskillsdemo.data.entities.EntityView
import github.mike12041204.aitanskillsdemo.data.entities.TargetView
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository
import github.mike12041204.aitanskillsdemo.ui.fieldCmToPx
import github.mike12041204.aitanskillsdemo.ui.fieldPxToCm
import github.mike12041204.aitanskillsdemo.ui.screenPxPerCm
import github.mike12041204.aitanskillsdemo.ui.theme.FIELD_BORDER_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.FIELD_DISCOVER_RANGE_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.FIELD_GRID_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.FIELD_PATH_COLOR
import github.mike12041204.aitanskillsdemo.ui.toOffset

private const val BORDER_STROKE_WIDTH_CM = 0.09f

private const val PATH_STROKE_WIDTH_CM = 0.03f
private const val PATH_DOT_LENGTH_CM = 0.01f
private const val PATH_DOT_GAP_CM = 0.1f

// How many times per second an exploding entity blinks off and back on
private const val EXPLODE_BLINKS_PER_SECOND = 6f

// Images for every kind of entity, loaded once in FieldScene and passed down to the drawing code
private class EntityIcons(
    val droneUnselected: Painter,
    val droneSelected: Painter,
    val targetUnselected: Painter,
    val targetSelected: Painter
)

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

    // Entity images, loaded here because images can only be loaded outside the drawing code
    val icons = EntityIcons(
        droneUnselected = painterResource(R.drawable.drone_unselected),
        droneSelected = painterResource(R.drawable.drone_selected),
        targetUnselected = painterResource(R.drawable.target_unselected),
        targetSelected = painterResource(R.drawable.target_selected)
    )

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
                    viewModel.onDrag(drag, size.toSize(), pxPerCm)
                }
            }
    ) {
        // Read version here causing canvas to redraw every field update
        version

        drawBorder(pan, fieldSize, pxPerCm)
        drawGrid(pan, fieldSize, pxPerCm)

        // Commands first so entities are drawn on top of them
        viewModel.entities.filterIsInstance<DroneView>()
            .forEach { drawDroneCommand(it, pan, pxPerCm) }
        viewModel.entities.forEach { drawEntity(it, pan, pxPerCm, icons) }
    }
}

// Outline around the whole field
private fun DrawScope.drawBorder(pan: Offset, fieldSize: Size, pxPerCm: Float) {
    drawRect(
        FIELD_BORDER_COLOR,
        topLeft = pan,
        size = fieldSize,
        style = Stroke(BORDER_STROKE_WIDTH_CM * pxPerCm)
    )
}

// 1 cm grid, vertical lines then horizontal lines
private fun DrawScope.drawGrid(pan: Offset, fieldSize: Size, pxPerCm: Float) {
    for (i in 1..<FieldRepository.FIELD_WIDTH_CM.toInt()) {
        val x = pan.x + i * pxPerCm
        drawLine(FIELD_GRID_COLOR, Offset(x, pan.y), Offset(x, pan.y + fieldSize.height))
    }
    for (i in 1..<FieldRepository.FIELD_HEIGHT_CM.toInt()) {
        val y = pan.y + i * pxPerCm
        drawLine(FIELD_GRID_COLOR, Offset(pan.x, y), Offset(pan.x + fieldSize.width, y))
    }
}

// Shows the part of the drone's path it has not reached yet, patrolling drones also show their discover range
private fun DrawScope.drawDroneCommand(drone: DroneView, pan: Offset, pxPerCm: Float) {
    if (drone.command == DroneView.DroneCommand.PATROL) {
        drawDiscoverRange(drone, pan, pxPerCm)
    }

    drawRemainingPath(drone, pan, pxPerCm)
}

// Light grey ring showing how close a target has to be for a patrolling drone to attack it
private fun DrawScope.drawDiscoverRange(drone: DroneView, pan: Offset, pxPerCm: Float) {
    drawCircle(
        FIELD_DISCOVER_RANGE_COLOR,
        radius = DroneView.PATROL_DISCOVER_DISTANCE_CM * pxPerCm,
        center = fieldCmToPx(drone.positionCm.toOffset(), pan, pxPerCm),
        style = Stroke(PATH_STROKE_WIDTH_CM * pxPerCm)
    )
}

// Dotted line from the drone through every point of its path it has not reached yet
private fun DrawScope.drawRemainingPath(drone: DroneView, pan: Offset, pxPerCm: Float) {
    val dronePath = drone.path ?: return

    // The dots start as far into their pattern as the drone has traveled, so they stay still on the field
    val dots = PathEffect.dashPathEffect(
        floatArrayOf(PATH_DOT_LENGTH_CM * pxPerCm, PATH_DOT_GAP_CM * pxPerCm),
        dronePath.distanceTraveledCm * pxPerCm
    )

    val start = fieldCmToPx(drone.positionCm.toOffset(), pan, pxPerCm)
    val line = Path()
    line.moveTo(start.x, start.y)

    for (point in dronePath.remainingPoints) {
        val next = fieldCmToPx(point.toOffset(), pan, pxPerCm)
        line.lineTo(next.x, next.y)
    }

    drawPath(
        line,
        FIELD_PATH_COLOR,
        style = Stroke(
            width = PATH_STROKE_WIDTH_CM * pxPerCm,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dots
        )
    )
}

// True during the hidden half of each blink, so an exploding entity starts visible
private fun isBlinkedOff(explodingSeconds: Float): Boolean {
    val halfBlinks = (explodingSeconds * EXPLODE_BLINKS_PER_SECOND * 2).toInt()
    return halfBlinks % 2 == 1
}

// Draws each kind of entity in its own look
private fun DrawScope.drawEntity(
    entity: EntityView,
    pan: Offset,
    pxPerCm: Float,
    icons: EntityIcons
) {
    // Exploding entities blink, nothing is drawn during the hidden half of each blink
    if (entity.isExploding && isBlinkedOff(entity.explodingSeconds)) {
        return
    }

    when (entity) {
        is DroneView -> drawDrone(entity, pan, pxPerCm, icons)
        is TargetView -> drawTarget(entity, pan, pxPerCm, icons)
    }
}

// Diamond icon, the drone's size in cm is used for both the width and height
private fun DrawScope.drawDrone(drone: DroneView, pan: Offset, pxPerCm: Float, icons: EntityIcons) {
    val painter: Painter

    if (drone.isSelected) {
        painter = icons.droneSelected
    } else {
        painter = icons.droneUnselected
    }

    val sizePx = drone.sizeCm * pxPerCm
    val center = fieldCmToPx(drone.positionCm.toOffset(), pan, pxPerCm)

    // Painters draw from their top left corner, so move back half the size from the center
    translate(center.x - sizePx / 2, center.y - sizePx / 2) {
        with(painter) {
            draw(Size(sizePx, sizePx))
        }
    }
}

// Rectangle icon, the target's size in cm is the width and the height keeps the image's shape
private fun DrawScope.drawTarget(
    target: TargetView,
    pan: Offset,
    pxPerCm: Float,
    icons: EntityIcons
) {
    val painter: Painter

    if (target.isSelected) {
        painter = icons.targetSelected
    } else {
        painter = icons.targetUnselected
    }

    val widthPx = target.sizeCm * pxPerCm
    val heightPx = widthPx * painter.intrinsicSize.height / painter.intrinsicSize.width
    val center = fieldCmToPx(target.positionCm.toOffset(), pan, pxPerCm)

    // Painters draw from their top left corner, so move back half the size from the center
    translate(center.x - widthPx / 2, center.y - heightPx / 2) {
        with(painter) {
            draw(Size(widthPx, heightPx))
        }
    }
}
