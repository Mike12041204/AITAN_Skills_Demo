package github.mike12041204.aitanskillsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.mike12041204.aitanskillsdemo.ui.debug.DebugScene
import github.mike12041204.aitanskillsdemo.ui.field.FieldScene
import github.mike12041204.aitanskillsdemo.ui.hotbar.HotbarScene
import github.mike12041204.aitanskillsdemo.ui.theme.AITANSkillsDemoTheme

// Shows extra information on screen for testing, turn off before release
private const val DEBUG_MODE = true

// App entry point, Android starts the app here
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Draw behind the status bar and navigation bar
        enableEdgeToEdge()

        setContent {
            AITANSkillsDemoTheme {
                // Surface gives the screen the theme background color
                Surface {
                    // Box stacks its children, so the hotbar is drawn on top of the field
                    Box {
                        FieldScene()
                        HotbarScene(modifier = Modifier.align(Alignment.BottomCenter))

                        if (DEBUG_MODE) {
                            DebugScene(modifier = Modifier.align(Alignment.TopEnd))
                        }
                    }
                }
            }
        }
    }

    // Run the simulation only while the app is on screen
    override fun onStart() {
        super.onStart()
        AppContainer.simulationClock.start()
    }

    override fun onStop() {
        super.onStop()
        AppContainer.simulationClock.stop()
    }
}
