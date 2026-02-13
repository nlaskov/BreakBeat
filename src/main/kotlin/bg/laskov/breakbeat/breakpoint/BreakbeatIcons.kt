package bg.laskov.breakbeat.breakpoint

import javax.swing.Icon
import com.intellij.openapi.util.IconLoader.getIcon

object BreakbeatIcons {
    val SOUND_BREAKPOINT: Icon =
        getIcon("/icons/breakpoint_on.svg", BreakbeatIcons::class.java)

    val SOUND_BREAKPOINT_DISABLED: Icon =
        getIcon("/icons/breakpoint_off.svg", BreakbeatIcons::class.java)

    val SOUND_BREAKPOINT_SUSPENDED_NONE: Icon =
        getIcon("/icons/breakpoint_nonesuspend.svg", BreakbeatIcons::class.java)
}