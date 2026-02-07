package bg.laskov

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "BreakpointSoundSettings", storages = [Storage("breakpointSound.xml")])
@Service(Level.APP)
class BreakpointSoundSettings {

    var soundFile: String? = "beep"

    companion object {
        val instance: BreakpointSoundSettings
            get() = com.intellij.openapi.application.ApplicationManager.getApplication().getService(
                BreakpointSoundSettings::class.java)
    }
}