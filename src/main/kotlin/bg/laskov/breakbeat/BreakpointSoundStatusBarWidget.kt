package bg.laskov.breakbeat

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.*
import java.awt.event.MouseEvent
import com.intellij.util.Consumer
import javax.swing.Icon

class BreakpointSoundStatusBarWidget (
    private val project: Project
) : StatusBarWidget, StatusBarWidget.IconPresentation {

    companion object {
        private val ENABLED_ICON: Icon by lazy {
            IconLoader.getIcon("/icons/sound_on.svg", ToggleBreakpointSoundAction::class.java)
        }
        private val DISABLED_ICON: Icon by lazy {
            IconLoader.getIcon("/icons/sound_off.svg", ToggleBreakpointSoundAction::class.java)
        }
    }

    override fun ID(): String = "BreakpointSoundStatus"

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getIcon(): Icon =
        if (ToggleBreakpointSoundAction.enabled) ENABLED_ICON else DISABLED_ICON

    override fun getTooltipText(): String =
        if (ToggleBreakpointSoundAction.enabled)
            "Breakpoint sound: ON"
        else
            "Breakpoint sound: OFF"

    override fun getClickConsumer(): Consumer<MouseEvent> =
        Consumer {
            ToggleBreakpointSoundAction.enabled =
                !ToggleBreakpointSoundAction.enabled

            val soundPlayer =
                com.intellij.openapi.application.ApplicationManager
                    .getApplication()
                    .getService(SoundPlayer::class.java)

            soundPlayer.setEnabled(ToggleBreakpointSoundAction.enabled)
            if (!ToggleBreakpointSoundAction.enabled) {
                soundPlayer.stop()
            }

            WindowManager.getInstance()
                .getStatusBar(project)
                ?.updateWidget(ID())

            WindowManager.getInstance()
                .getStatusBar(project)
                ?.component?.repaint()
        }

    override fun dispose() {}
}