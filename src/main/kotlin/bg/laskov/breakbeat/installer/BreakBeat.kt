package bg.laskov.breakbeat.installer

import bg.laskov.breakbeat.BreakpointSoundState
import bg.laskov.breakbeat.SoundPlayer
import bg.laskov.breakbeat.breakpoint.SoundBreakpointHitHighlighter
import bg.laskov.breakbeat.listeners.BreakpointSoundListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class BreakBeat : ProjectActivity {

    private val soundPlayer =
        ApplicationManager.getApplication().getService(SoundPlayer::class.java)

    override suspend fun execute(project: Project) {
        BreakpointSoundListener(project).attachListener()
        SoundBreakpointHitHighlighter(project)
        println("Preloading file for project: ${project.name}")

        val settings = BreakpointSoundState.getInstance()
        soundPlayer.reload(
            settings.state.selectedSoundPath,
            settings.state.volume,
            settings.state.enabled)
    }
}