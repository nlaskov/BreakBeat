package bg.laskov.breakbeat

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class BreakpointSoundStatusBarWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = "BreakpointSoundStatus"

    override fun getDisplayName(): String = "Breakpoint Sound"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget =
        BreakpointSoundStatusBarWidget(project)
}