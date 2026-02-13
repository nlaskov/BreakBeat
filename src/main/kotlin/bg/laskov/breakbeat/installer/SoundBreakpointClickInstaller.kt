package bg.laskov.breakbeat.installer

import bg.laskov.breakbeat.listeners.SoundBreakpointGutterDoubleClickListener
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class SoundBreakpointClickInstaller : ProjectActivity {

    override suspend fun execute(project: Project) {
        val listener = SoundBreakpointGutterDoubleClickListener(project)

        EditorFactory.getInstance().eventMulticaster.addEditorMouseListener(listener, project)

        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
            override fun editorCreated(event: EditorFactoryEvent) {
                event.editor.addEditorMouseListener(listener)
            }

            override fun editorReleased(event: EditorFactoryEvent) {
                event.editor.removeEditorMouseListener(listener)
            }
        }, project)
    }
}