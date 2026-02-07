package bg.laskov

import com.intellij.openapi.options.Configurable
import javax.swing.*
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout

class BreakpointSoundConfigurable : Configurable {

    private var panel: JPanel? = null
    private lateinit var soundComboBox: ComboBox<String>

    // Example list of sounds
    private val sounds = listOf("beep", "cow", "chicken")

    private var selectedSound: String? = "beep"

    override fun createComponent(): JComponent? {
        soundComboBox = ComboBox(sounds.toTypedArray())
        soundComboBox.addActionListener {
            selectedSound = soundComboBox.selectedItem as String
        }

        // Load the current selection from settings
        selectedSound = BreakpointSoundSettings.instance.soundFile
        if (selectedSound != null) {
            soundComboBox.selectedItem = selectedSound
        }

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Sound", soundComboBox)
            .panel

        panel = JPanel(BorderLayout())
        panel!!.add(formPanel, BorderLayout.NORTH)

        return panel
    }

    override fun isModified(): Boolean {
        return selectedSound != BreakpointSoundSettings.instance.soundFile
    }

    override fun apply() {
        BreakpointSoundSettings.instance.soundFile = selectedSound
    }

    override fun getDisplayName(): String {
        return "Breakpoint Sound"
    }
}