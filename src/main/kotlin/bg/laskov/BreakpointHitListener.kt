package bg.laskov

import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointImpl
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import java.util.concurrent.atomic.AtomicReference

class BreakpointSoundListener(private val project: Project) {

    private val LOG = Logger.getInstance(BreakpointSoundListener::class.java)
    private val currentClip = AtomicReference<Clip?>()

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

            val soundFileName = BreakpointSoundSettings.instance.soundFile ?: return
            val resourcePath = "sounds/$soundFileName.wav"
            val url = BreakpointSoundListener::class.java.classLoader.getResource(resourcePath)
                ?: throw IllegalStateException("Sound resource not found: $resourcePath")
            val audioInputStream = AudioSystem.getAudioInputStream(url)
            val clip: Clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()

            currentClip.set(clip)
            LOG.info("Playing breakpoint sound!")
        } catch (ex: Exception) {
            LOG.error("Failed to play sound", ex)
        }
    }

    private fun stopSound() {
        currentClip.getAndSet(null)?.let {
            it.stop()
            it.close()
        }
    }

}