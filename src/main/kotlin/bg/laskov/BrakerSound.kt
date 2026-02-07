package bg.laskov

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class BrakerSound : ProjectActivity {
    override suspend fun execute(project: Project) {
        BreakpointSoundListener(project).attachListener()
    }
}