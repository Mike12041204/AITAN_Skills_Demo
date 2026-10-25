package github.mike12041204.aitanskillsdemo.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import github.mike12041204.aitanskillsdemo.AppContainer

// What one hotbar button does, onClick runs when tapped if condition is true, otherwise failMessage is shown
data class HotbarAction(
    @DrawableRes val icon: Int,
    val description: String,
    val failMessage: String,
    val condition: () -> Boolean,
    val onClick: () -> Unit
)

// Holds the hotbar buttons and connects them to the field
class HotbarViewModel : ViewModel() {

    // Commands for the drones on the same field the FieldViewModel uses
    private val droneCommands = AppContainer.droneCommands

    // Last fail message, and a count that goes up each time so the same message can show again
    var message by mutableStateOf("")
        private set
    var messageCount by mutableIntStateOf(0)
        private set

    // Add an entry here to add a button to the hotbar
    val actions = listOf(
        HotbarAction(
            icon = android.R.drawable.ic_menu_add,
            description = "Straight attack",
            failMessage = "Select one or more drones and one target",
            condition = { droneCommands.canStraightAttack() },
            onClick = { droneCommands.straightAttack() }
        ),
        HotbarAction(
            icon = android.R.drawable.ic_menu_delete,
            description = "Evasive attack",
            failMessage = "Select one or more drones and one target",
            condition = { droneCommands.canEvasiveAttack() },
            onClick = { droneCommands.evasiveAttack() }
        ),
        HotbarAction(
            icon = android.R.drawable.ic_menu_info_details,
            description = "Coordinated attack",
            failMessage = "Select one or more drones and one target",
            condition = { droneCommands.canCoordinatedAttack() },
            onClick = { droneCommands.coordinatedAttack() }
        ),
        HotbarAction(
            icon = android.R.drawable.ic_media_play,
            description = "Patrol",
            failMessage = "Select one or more drones",
            condition = { droneCommands.canPatrol() },
            onClick = { droneCommands.patrol() }
        ),
        HotbarAction(
            icon = android.R.drawable.ic_menu_close_clear_cancel,
            description = "Cancel",
            failMessage = "Select one or more drones",
            condition = { droneCommands.canCancel() },
            onClick = { droneCommands.cancel() }
        ),
    )

    // Runs the action if its condition is met, otherwise shows its fail message
    // Returns true if the action ran
    fun onActionTapped(action: HotbarAction): Boolean {
        if (action.condition()) {
            action.onClick()
            return true
        } else {
            message = action.failMessage
            messageCount++
            return false
        }
    }
}
