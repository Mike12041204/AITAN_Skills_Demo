package github.mike12041204.aitanskillsdemo.ui.field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import github.mike12041204.aitanskillsdemo.AppContainer
import github.mike12041204.aitanskillsdemo.data.entities.EntityView
import github.mike12041204.aitanskillsdemo.data.field.FieldRepository
import github.mike12041204.aitanskillsdemo.ui.toOffset
import io.reactivex.rxjava3.core.Observable

// Smallest tap radius in cm so tiny entities are still easy to hit
private const val MIN_TAP_RADIUS_CM = 0.35f

// Holds the field screen state and survives screen rotations
class FieldViewModel : ViewModel() {

    private val repo = AppContainer.fieldRepository

    // Goes up every time the field changes, the screen redraws when it does
    val version: Observable<Long> = repo.version

    // Every entity on the field, read only
    val entities: List<EntityView> get() = repo.entityViews

    // How far the field is dragged, in cm, so the view stays put across rotations
    var panCm by mutableStateOf(Offset.Zero)
        private set

    // Moves the field by drag pixels, stopping at the field edges
    // viewportSize is the size of the screen area showing the field, in pixels
    fun onDrag(drag: Offset, viewportSize: Size, pxPerCm: Float) {
        val fieldWidthPx = FieldRepository.FIELD_WIDTH_CM * pxPerCm
        val fieldHeightPx = FieldRepository.FIELD_HEIGHT_CM * pxPerCm
        val newPanPx = panCm * pxPerCm + drag

        panCm = Offset(
            newPanPx.x.coerceIn(minOf(0f, viewportSize.width - fieldWidthPx), 0f),
            newPanPx.y.coerceIn(minOf(0f, viewportSize.height - fieldHeightPx), 0f)
        ) / pxPerCm
    }

    // Point is in field cm, toggles the entity closest to the tap if it is close enough
    // Exploding entities are ignored so they can never be selected
    fun onFieldTapped(point: Offset) {
        entities
            .filter { !it.isExploding }
            .filter { distanceCm(it, point) <= maxOf(it.sizeCm / 2, MIN_TAP_RADIUS_CM) }
            .minByOrNull { distanceCm(it, point) }
            ?.let {
                repo.toggleSelection(it)
            }
    }

    // Distance in cm from the center of the entity to the point
    private fun distanceCm(entity: EntityView, point: Offset): Float {
        return (entity.positionCm.toOffset() - point).getDistance()
    }
}
