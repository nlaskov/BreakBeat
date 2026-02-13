package bg.laskov.breakbeat.breakpoint

import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties
import javax.swing.Icon

class SoundJavaLineBreakpointType :
    JavaLineBreakpointType("sound-java-line", "Sound Java Breakpoint") {

    companion object {
        fun getInstance(): SoundJavaLineBreakpointType =
            EXTENSION_POINT_NAME.extensionList
                .filterIsInstance<SoundJavaLineBreakpointType>()
                .first()
    }

    override fun createBreakpointProperties(
        file: VirtualFile,
        line: Int
    ): JavaLineBreakpointProperties {
        return JavaLineBreakpointProperties()
    }

    override fun getEnabledIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getDisabledIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT_DISABLED
    }

    override fun getPendingIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getTemporaryIcon(): Icon? {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getInactiveDependentIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getSuspendNoneIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getMutedEnabledIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }

    override fun getMutedDisabledIcon(): Icon {
        return BreakbeatIcons.SOUND_BREAKPOINT
    }
}