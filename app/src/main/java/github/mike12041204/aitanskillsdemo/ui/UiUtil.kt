package github.mike12041204.aitanskillsdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalResources
import github.mike12041204.aitanskillsdemo.data.Entity
import github.mike12041204.aitanskillsdemo.data.FieldRepository

private const val CM_PER_INCH = 2.54f

// Screen pixels per real life centimeter
@Composable
fun screenPxPerCm() = LocalResources.current.displayMetrics.xdpi / CM_PER_INCH

// Entity position in centimeters to screen pixels, y flipped so it grows upward
fun entityScreenPosition(entity: Entity, pan: Offset, pxPerCm: Float) = Offset(
    x = pan.x + entity.xPosition * pxPerCm,
    y = pan.y + (FieldRepository.FIELD_HEIGHT - entity.yPosition) * pxPerCm
)

// Screen pixels to field centimeters, the reverse of entityScreenPosition
fun screenToField(point: Offset, pan: Offset, pxPerCm: Float) = Offset(
    x = (point.x - pan.x) / pxPerCm,
    y = FieldRepository.FIELD_HEIGHT - (point.y - pan.y) / pxPerCm
)
