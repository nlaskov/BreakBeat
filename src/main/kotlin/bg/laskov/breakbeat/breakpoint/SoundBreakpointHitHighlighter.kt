package bg.laskov.breakbeat.breakpoint

import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class SoundBreakpointHitHighlighter(project: Project) {

    private val connection = project.messageBus.connect()

    init {
        connection.subscribe(XDebuggerManager.TOPIC, object : com.intellij.xdebugger.XDebuggerManagerListener {
            override fun currentSessionChanged(previousSession: XDebugSession?, currentSession: XDebugSession?) {
                currentSession?.addSessionListener(object : XDebugSessionListener {
                    override fun sessionPaused() {
                        updateHitIcon(project, currentSession, hit = true)
                    }

                    override fun sessionResumed() {
                        updateHitIcon(project, currentSession, hit = false)
                    }

                    override fun sessionStopped() {
                        updateHitIcon(project, currentSession, hit = false)
                    }
                })
            }
        })
    }

    private fun updateHitIcon(project: Project, session: XDebugSession, hit: Boolean) {
        val pos = session.currentPosition ?: return
        val fileUrl = pos.file.url
        val line = pos.line

        val bm = XDebuggerManager.getInstance(project).breakpointManager

        // Find *your* line breakpoint at the current position
        val soundBp = bm.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .firstOrNull { it.fileUrl == fileUrl && it.line == line && it.type is SoundJavaLineBreakpointType }
            ?: return

        if (hit) {
            bm.updateBreakpointPresentation(soundBp, BreakbeatIcons.SOUND_BREAKPOINT, null)
        } else {
            // Restore default icon/presentation chosen by platform
            bm.updateBreakpointPresentation(soundBp, null, null)
        }
    }
}