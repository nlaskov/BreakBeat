package bg.laskov.breakbeat.listeners

import bg.laskov.breakbeat.breakpoint.SoundJavaLineBreakpointType
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties

class SoundBreakpointGutterDoubleClickListener(private val project: Project) : EditorMouseListener {

    private val logger = Logger.getInstance(SoundBreakpointGutterDoubleClickListener::class.java)

    override fun mousePressed(e: EditorMouseEvent) {
        if (e.mouseEvent.clickCount != 2) return
        if (e.area != EditorMouseEventArea.LINE_NUMBERS_AREA) return

        e.mouseEvent.consume()

        val editor = e.editor
        val document = editor.document
        val vFile = FileDocumentManager.getInstance().getFile(document) ?: return

        val logicalPos = editor.xyToLogicalPosition(e.mouseEvent.point)
        val line = logicalPos.line
        if (line < 0 || line >= document.lineCount) return

        logger.info("Double clicked on line $line")
        ApplicationManager.getApplication().invokeLater {
            convertLineToSoundBreakpoint(project, vFile.url, line)
        }
    }

    private fun convertLineToSoundBreakpoint(project: Project, fileUrl: String, line: Int) {
        val bm = XDebuggerManager.getInstance(project).breakpointManager

        val soundType = SoundJavaLineBreakpointType.getInstance()

        val existing = bm.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == fileUrl && it.line == line }

        existing
            .filter { it.type != soundType }
            .forEach { bm.removeBreakpoint(it) }

        val alreadySound = existing.any { it.type == soundType }
        if (alreadySound) return

        bm.addLineBreakpoint(soundType, fileUrl, line, JavaLineBreakpointProperties())
    }
}