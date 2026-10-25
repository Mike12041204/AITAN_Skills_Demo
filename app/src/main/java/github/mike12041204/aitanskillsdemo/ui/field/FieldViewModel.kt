package github.mike12041204.aitanskillsdemo.ui.field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import github.mike12041204.aitanskillsdemo.AppContainer
import github.mike12041204.aitanskillsdemo.data.entities.Entity
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
    val entities: List<Entity> get() = repo.entities

    // How far the field is dragged, in cm, so the view stays put across rotations
    var panCm by mutableStateOf(Offset.Zero)

    // Point is in field cm, toggles the entity closest to the tap if it is close enough
    // Exploding entities are ignored so they can never be selected
    fun onFieldTapped(point: Offset) {
        entities
            .filter { !it.exploding }
            .filter { distanceCm(it, point) <= maxOf(it.sizeCm / 2, MIN_TAP_RADIUS_CM) }
            .minByOrNull { distanceCm(it, point) }
            ?.let {
                repo.toggleSelection(it)
            }
    }

    // Distance in cm from the center of the entity to the point
    private fun distanceCm(entity: Entity, point: Offset): Float {
        return (entity.positionCm.toOffset() - point).getDistance()
    }
}
