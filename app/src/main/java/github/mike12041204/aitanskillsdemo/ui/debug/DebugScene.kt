package github.mike12041204.aitanskillsdemo.ui.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import github.mike12041204.aitanskillsdemo.ui.cmToDp
import github.mike12041204.aitanskillsdemo.ui.theme.DEBUG_TEXT_COLOR

private const val DEBUG_PADDING_CM = 0.15f

// Information for testing, only shown when debug mode is on
@Composable
fun DebugScene(modifier: Modifier = Modifier, viewModel: DebugViewModel = viewModel()) {
    val frameCount by viewModel.frameCount.subscribeAsState(0L)

    Text(
        frameCount.toString(),
        color = DEBUG_TEXT_COLOR,
        modifier = modifier
            .statusBarsPadding()
            .padding(cmToDp(DEBUG_PADDING_CM))
    )
}
