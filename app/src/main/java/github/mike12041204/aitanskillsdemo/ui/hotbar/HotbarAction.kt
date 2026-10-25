package github.mike12041204.aitanskillsdemo.ui.hotbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// What one hotbar button does, onClick runs when tapped if condition is true, otherwise failMessage is shown
data class HotbarAction(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    @StringRes val failMessage: Int,
    val condition: () -> Boolean,
    val onClick: () -> Unit
)
