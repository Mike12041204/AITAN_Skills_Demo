package github.mike12041204.aitanskillsdemo.ui.hotbar

import androidx.annotation.DrawableRes

// What one hotbar button does, onClick runs when tapped if condition is true, otherwise failMessage is shown
data class HotbarAction(
    @DrawableRes val icon: Int,
    val description: String,
    val failMessage: String,
    val condition: () -> Boolean,
    val onClick: () -> Unit
)
