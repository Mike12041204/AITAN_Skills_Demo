package github.mike12041204.aitanskillsdemo.ui.debug

import androidx.lifecycle.ViewModel
import github.mike12041204.aitanskillsdemo.AppContainer
import io.reactivex.rxjava3.core.Observable

// Holds the information the debug overlay shows
class DebugViewModel : ViewModel() {

    // Frames run since the app started
    val frameCount: Observable<Long> = AppContainer.simulationClock.frameCount
}
