package bg.laskov.breakbeat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import javax.swing.Icon
import javax.swing.ImageIcon

class ToggleBreakpointSoundAction : AnAction("Toggle Breakpoint Sound") {

    companion object {
        // Use emoji as icon text (works as icon text in toolbar)
        private val ENABLED_ICON: Icon by lazy {
            ImageIcon(ToggleBreakpointSoundAction::class.java.getResource("/icons/green.svg.png"))
        }
        private val DISABLED_ICON: Icon by lazy {
            ImageIcon(ToggleBreakpointSoundAction::class.java.getResource("/icons/red.svg.png"))
        }
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

    override fun update(e: AnActionEvent) {
        // Swap icon dynamically
        e.presentation.icon = if (enabled) ENABLED_ICON else DISABLED_ICON
        e.presentation.text = "" // optional: no text, just icon
    }
}