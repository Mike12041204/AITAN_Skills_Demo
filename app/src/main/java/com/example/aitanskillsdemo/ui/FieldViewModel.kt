package com.example.aitanskillsdemo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.aitanskillsdemo.data.Entity
import com.example.aitanskillsdemo.data.FieldRepository
import io.reactivex.rxjava3.core.Observable

// Smallest tap radius in cm so tiny entities are still easy to hit
private const val MIN_TAP_RADIUS_CM = 0.35f

// Keeps the field alive across screen rotations
class FieldViewModel : ViewModel() {

    private val repo = FieldRepository()

    val version: Observable<Long> = repo.version
    val entities: List<Entity> get() = repo.entities

    // How far the field is dragged, in cm, so the view stays put across rotations
    var panCm by mutableStateOf(Offset.Zero)

    // Point is in field cm
    fun onFieldTapped(point: Offset) {
        entities.firstOrNull {
            (Offset(it.xPosition, it.yPosition) - point).getDistance() <= maxOf(
                it.size / 2,
                MIN_TAP_RADIUS_CM
            )
        }?.let { repo.toggleSelection(it) }
    }
}
