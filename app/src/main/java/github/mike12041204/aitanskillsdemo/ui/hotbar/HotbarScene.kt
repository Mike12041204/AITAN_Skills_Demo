package github.mike12041204.aitanskillsdemo.ui.hotbar

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import github.mike12041204.aitanskillsdemo.ui.cmToDp
import github.mike12041204.aitanskillsdemo.ui.theme.HOTBAR_BUTTON_BORDER_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.HOTBAR_BUTTON_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.HOTBAR_BUTTON_FAIL_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.HOTBAR_BUTTON_FLASH_COLOR
import github.mike12041204.aitanskillsdemo.ui.theme.HOTBAR_MESSAGE_COLOR
import kotlinx.coroutines.launch

// All sizes in real life centimeters
private const val BUTTON_SIZE_CM = 0.9f
private const val BUTTON_SPACING_CM = 0.15f
private const val ICON_PADDING_CM = 0.15f

private const val BUTTON_BORDER_WIDTH_CM = 0.03f
private const val FLASH_DURATION_MS = 300

private const val MESSAGE_SHOW_MS = 1000
private const val MESSAGE_FADE_MS = 500

// Row of square buttons with a fading message above it for when a button can't be used
@Composable
fun HotbarScene(modifier: Modifier = Modifier, viewModel: HotbarViewModel = viewModel()) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(cmToDp(BUTTON_SPACING_CM)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(cmToDp(BUTTON_SPACING_CM))
    ) {
        // Empty until a button fails, the message is transparent then anyway
        val message = viewModel.message?.let { stringResource(it) } ?: ""
        HotbarMessage(message, viewModel.messageCount)

        Row(
            horizontalArrangement = Arrangement.spacedBy(cmToDp(BUTTON_SPACING_CM))
        ) {
            viewModel.actions.forEach { action ->
                HotbarButton(action, onTapped = { viewModel.onActionTapped(action) })
            }
        }
    }
}

// Red message that shows, then fades away each time messageCount goes up
@Composable
private fun HotbarMessage(message: String, messageCount: Int) {
    // Starts transparent so nothing shows until a button fails
    val color = remember { Animatable(Color.Transparent) }

    LaunchedEffect(messageCount) {
        if (messageCount > 0) {
            color.snapTo(HOTBAR_MESSAGE_COLOR)
            color.animateTo(
                HOTBAR_MESSAGE_COLOR.copy(alpha = 0f),
                tween(MESSAGE_FADE_MS, delayMillis = MESSAGE_SHOW_MS)
            )
        }
    }

    // Live region so screen readers read the message out when it changes
    Text(
        message,
        color = color.value,
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
    )
}

// One square button that flashes blue if its action ran or red if it didn't
// onTapped returns true if the action ran
@Composable
private fun HotbarButton(action: HotbarAction, onTapped: () -> Boolean) {
    // Each button gets its own background color so only the tapped one flashes
    val background = remember { Animatable(HOTBAR_BUTTON_COLOR) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(cmToDp(BUTTON_SIZE_CM))
            .background(background.value)
            .border(cmToDp(BUTTON_BORDER_WIDTH_CM), HOTBAR_BUTTON_BORDER_COLOR)
            .clickable {
                // Runs the action if its condition is met, otherwise shows its fail message
                val actionRan = onTapped()

                val flashColor: Color

                if (actionRan) {
                    flashColor = HOTBAR_BUTTON_FLASH_COLOR
                } else {
                    flashColor = HOTBAR_BUTTON_FAIL_COLOR
                }

                // Flash, then fade back to the normal color
                scope.launch {
                    background.snapTo(flashColor)
                    background.animateTo(HOTBAR_BUTTON_COLOR, tween(FLASH_DURATION_MS))
                }
            }
            .padding(cmToDp(ICON_PADDING_CM))
    ) {
        Image(
            painter = painterResource(action.icon),
            contentDescription = stringResource(action.description)
        )
    }
}
