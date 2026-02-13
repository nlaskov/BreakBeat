package bg.laskov.breakbeat.ui

import bg.laskov.breakbeat.BreakpointSoundState
import bg.laskov.breakbeat.SoundPlayer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.*
import java.awt.event.MouseEvent
import com.intellij.util.Consumer
import javax.swing.Icon

class BreakpointSoundStatusBarWidget(
    private val project: Project
) : StatusBarWidget, StatusBarWidget.IconPresentation {

    private val settings = BreakpointSoundState.getInstance()

    companion object {
        private val ENABLED_ICON: Icon by lazy {
            IconLoader.getIcon("/icons/sound_on.svg", BreakpointSoundStatusBarWidget::class.java)
        }
        private val DISABLED_ICON: Icon by lazy {
            IconLoader.getIcon("/icons/sound_off.svg", BreakpointSoundStatusBarWidget::class.java)
        }
    }

    override fun ID(): String = "BreakpointSoundStatus"

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getIcon(): Icon =
        if (settings.state.enabled) ENABLED_ICON else DISABLED_ICON

    override fun getTooltipText(): String =
        if (settings.state.enabled)
            "Breakpoint sound: ON"
        else
            "Breakpoint sound: OFF"

    override fun getClickConsumer(): Consumer<MouseEvent> =
        Consumer {
            settings.state.enabled = !settings.state.enabled

            val soundPlayer =
                ApplicationManager
                    .getApplication()
                    .getService(SoundPlayer::class.java)

            soundPlayer.setEnabled(settings.state.enabled)
            if (!settings.state.enabled) {
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