package github.mike12041204.aitanskillsdemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Dp
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository
import github.mike12041204.aitanskillsdemo.data.field.PointCm

private const val CM_PER_INCH = 2.54f

// Screen pixels per real life centimeter
@Composable
fun screenPxPerCm() = LocalResources.current.displayMetrics.xdpi / CM_PER_INCH

// Real life centimeters to dp, for sizing regular UI elements
@Composable
fun cmToDp(cm: Float): Dp = with(LocalDensity.current) { (cm * screenPxPerCm()).toDp() }

// Field centimeters to screen pixels, y flipped so it grows upward
fun fieldCmToPx(point: Offset, pan: Offset, pxPerCm: Float) = Offset(
    x = pan.x + point.x * pxPerCm,
    y = pan.y + (FieldRepository.FIELD_HEIGHT_CM - point.y) * pxPerCm
)

// Screen pixels to field centimeters, the reverse of fieldCmToPx
fun fieldPxToCm(point: Offset, pan: Offset, pxPerCm: Float) = Offset(
    x = (point.x - pan.x) / pxPerCm,
    y = FieldRepository.FIELD_HEIGHT_CM - (point.y - pan.y) / pxPerCm
)

// Data layer point in cm as a Compose Offset in cm, so it can be used with the functions above
fun PointCm.toOffset(): Offset = Offset(xCm, yCm)
