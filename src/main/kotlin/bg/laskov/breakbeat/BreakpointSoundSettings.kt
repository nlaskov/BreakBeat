package bg.laskov.breakbeat

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Level.APP)
@State(
    name = "BreakpointSoundSettings",
    storages = [Storage("breakpoint-sound.xml")]
)
class BreakpointSoundSettings : PersistentStateComponent<BreakpointSoundSettings.State> {

    data class State(
        var enabled: Boolean = true,
        var selectedSoundPath: String = "beep",
        var volume: Float = 1.0f,
        var initialized: Boolean = false
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state

        if (!state.initialized) {
            state.selectedSoundPath = state.selectedSoundPath
            state.enabled = state.enabled
            state.volume = state.volume
            state.initialized = true
        }

    }

    companion object {
        fun getInstance(): BreakpointSoundSettings =
            com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(BreakpointSoundSettings::class.java)
    }
}