package github.mike12041204.aitanskillsdemo.ui.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import github.mike12041204.aitanskillsdemo.AppContainer
import github.mike12041204.aitanskillsdemo.ui.cmToDp

private val DEBUG_TEXT_COLOR = Color(0xFF00C853) // Green
private const val DEBUG_PADDING_CM = 0.15f

// Information for testing, only shown when debug mode is on
@Composable
fun DebugScene(modifier: Modifier = Modifier) {
    val frameCount by AppContainer.simulationClock.frameCount.subscribeAsState(0L)

    Text(
        frameCount.toString(),
        color = DEBUG_TEXT_COLOR,
        modifier = modifier
            .statusBarsPadding()
            .padding(cmToDp(DEBUG_PADDING_CM))
    )
}
