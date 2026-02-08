package bg.laskov.breakbeat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class ToggleBreakpointSoundAction : AnAction("Toggle Breakpoint Sound") {

    companion object {
        var enabled = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        enabled = !enabled

        val soundPlayer = ApplicationManager.getApplication().getService(SoundPlayer::class.java)
        soundPlayer.enabled = enabled

        if(!enabled) {
            soundPlayer.stop()
        }
    }
}