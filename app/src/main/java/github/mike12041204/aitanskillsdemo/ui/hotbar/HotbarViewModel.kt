package github.mike12041204.aitanskillsdemo.ui.hotbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import github.mike12041204.aitanskillsdemo.AppContainer
import github.mike12041204.aitanskillsdemo.R

// Holds the hotbar buttons and connects them to the field
class HotbarViewModel : ViewModel() {

    // Commands for the drones on the same field the FieldViewModel uses
    private val droneCommands = AppContainer.droneCommands

    // String resource of the last fail message, null until a button fails
    // The count goes up each time so the same message can show again
    var message by mutableStateOf<Int?>(null)
        private set
    var messageCount by mutableIntStateOf(0)
        private set

    // Add an entry here to add a button to the hotbar
    val actions = listOf(
        HotbarAction(
            icon = R.drawable.ic_straight_attack,
            description = R.string.hotbar_straight_attack,
            failMessage = R.string.hotbar_straight_attack_fail,
            condition = { droneCommands.canStraightAttack() },
            onClick = { droneCommands.straightAttack() }
        ),
        HotbarAction(
            icon = R.drawable.ic_evasive_attack,
            description = R.string.hotbar_evasive_attack,
            failMessage = R.string.hotbar_evasive_attack_fail,
            condition = { droneCommands.canEvasiveAttack() },
            onClick = { droneCommands.evasiveAttack() }
        ),
        HotbarAction(
            icon = R.drawable.ic_coordinated_attack,
            description = R.string.hotbar_coordinated_attack,
            failMessage = R.string.hotbar_coordinated_attack_fail,
            condition = { droneCommands.canCoordinatedAttack() },
            onClick = { droneCommands.coordinatedAttack() }
        ),
        HotbarAction(
            icon = R.drawable.ic_patrol,
            description = R.string.hotbar_patrol,
            failMessage = R.string.hotbar_patrol_fail,
            condition = { droneCommands.canPatrol() },
            onClick = { droneCommands.patrol() }
        ),
        HotbarAction(
            icon = R.drawable.ic_cancel,
            description = R.string.hotbar_cancel,
            failMessage = R.string.hotbar_cancel_fail,
            condition = { droneCommands.canCancel() },
            onClick = { droneCommands.cancel() }
        )
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
