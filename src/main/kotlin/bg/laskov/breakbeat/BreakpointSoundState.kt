package bg.laskov.breakbeat

import bg.laskov.breakbeat.enums.BreakpointControlMode
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
class BreakpointSoundState : PersistentStateComponent<BreakpointSoundState.State> {

    data class State(
        var enabled: Boolean = true,
        var selectedSoundPath: String = "beep",
        var volume: Float = 50f,
        var initialized: Boolean = false,
        var breakpointControlMode: BreakpointControlMode = BreakpointControlMode.ALL
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
            state.breakpointControlMode = state.breakpointControlMode
        }

    }

    companion object {
        fun getInstance(): BreakpointSoundState =
            com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(BreakpointSoundState::class.java)
    }
}