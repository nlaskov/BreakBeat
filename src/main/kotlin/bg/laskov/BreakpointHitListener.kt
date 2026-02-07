package bg.laskov

import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointImpl
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class BreakpointSoundListener(private val project: Project) {

    private val LOG = Logger.getInstance(BreakpointSoundListener::class.java)

    fun attachListener() {
        LOG.info("Breakpoint listener attached for project: ${project.name}")

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
                                    .filter { it is XLineBreakpointImpl }
                                    .first { bp ->

                                        // match by file + line number
                                        ((bp as XLineBreakpointImpl).file?.name
                                            ?: "") == pos.file.name && (bp as XLineBreakpointImpl).line == pos.line
                                    }
                            }

                            if (breakpoint != null) {
                                playSoundForBreakpoint()
                            }
                        }
                    })
                }
            }
        )
    }

    private fun playSoundForBreakpoint() {
        try {
            val soundFileName = BreakpointSoundSettings.instance.soundFile ?: return
            val soundFile = File("/Users/nikolalaskov/Desktop/BrakerSound/src/main/resources/sounds/$soundFileName.wav") // Replace with your WAV file path
            val audioInputStream = AudioSystem.getAudioInputStream(soundFile)
            val clip: Clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()
            LOG.info("Playing breakpoint sound!")
        } catch (ex: Exception) {
            LOG.error("Failed to play sound", ex)
        }
    }

}