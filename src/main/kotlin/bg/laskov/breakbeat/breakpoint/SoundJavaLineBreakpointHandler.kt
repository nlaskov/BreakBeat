package bg.laskov.breakbeat.breakpoint

import com.intellij.debugger.engine.DebugProcessImpl
import com.intellij.debugger.engine.JavaBreakpointHandler

class SoundJavaLineBreakpointHandler(
    process: DebugProcessImpl
) : JavaBreakpointHandler(SoundJavaLineBreakpointType::class.java, process) {}