package bg.laskov.breakbeat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class BreakpointSoundListener(private val project: Project) {

    private val logger = Logger.getInstance(BreakpointSoundListener::class.java)
    private val soundPlayer =
        ApplicationManager.getApplication().getService(SoundPlayer::class.java)

    fun attachListener() {
        logger.info("Breakpoint listener attached for project: ${project.name}")

        project.messageBus.connect().subscribe(
            XDebuggerManager.TOPIC,
            object : XDebuggerManagerListener {
                override fun processStarted(debugProcess: XDebugProcess) {
                    debugProcess.session.addSessionListener(object : XDebugSessionListener {
                        override fun sessionPaused() {
                            val session = debugProcess.session

                            // Get the current breakpoint(s) causing this pause
                            val breakpoint = session.currentPosition?.let { pos ->
                                XDebuggerManager.getInstance(project)
                                    .breakpointManager
                                    .allBreakpoints
                                    .filterIsInstance<XLineBreakpoint<XBreakpointProperties<Any>>>()
                                    .first { bp ->
                                        // match by file + line number
                                        (bp.shortFilePath ?: "") == pos.file.name && bp.line == pos.line
                                    }
                            }

                            if (breakpoint != null) {
                                playSoundForBreakpoint()
                            }
                        }

                        override fun sessionResumed() {
                            stopSound()
                        }

                        override fun sessionStopped() {
                            stopSound()
                        }
                    })
                }
            }
        )
    }

    private fun playSoundForBreakpoint() {
        try {
            stopSound() // stop any previous sound first

            soundPlayer.play()

            logger.info("Playing breakpoint sound!")
        } catch (ex: Exception) {
            logger.error("Failed to play sound", ex)
        }
    }

    private fun stopSound() {
        soundPlayer.stop()
    }

}