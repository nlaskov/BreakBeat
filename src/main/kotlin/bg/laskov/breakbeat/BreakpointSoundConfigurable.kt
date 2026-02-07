package bg.laskov.breakbeat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.*
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout

class BreakpointSoundConfigurable : Configurable {

    private var panel: JPanel? = null
    private lateinit var soundComboBox: ComboBox<String>
    private lateinit var volumeSlider: JSlider

    // Example list of sounds
    private val sounds = listOf("beep", "cow", "chicken")

    private var selectedSound: String? = "beep"
    private var selectedVolume: Float = 50F

    private val soundPlayer =
        ApplicationManager.getApplication().getService(SoundPlayer::class.java)

    override fun createComponent(): JComponent? {
        soundComboBox = ComboBox(sounds.toTypedArray())
        soundComboBox.addActionListener {
            selectedSound = soundComboBox.selectedItem as String
        }

        volumeSlider = JSlider(0, 100, selectedVolume.toInt())
        volumeSlider.majorTickSpacing = 20
        volumeSlider.minorTickSpacing = 5
        volumeSlider.paintTicks = true
        volumeSlider.paintLabels = true
        volumeSlider.addChangeListener {
            selectedVolume = volumeSlider.value.toFloat()
        }


        // Load the current selection from settings
        selectedSound = soundPlayer.getCurrentSoundFile()
        soundComboBox.selectedItem = selectedSound

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Sound", soundComboBox)
            .addLabeledComponent("Volume", volumeSlider)
            .panel

        panel = JPanel(BorderLayout())
        panel!!.add(formPanel, BorderLayout.NORTH)

        return panel
    }

    override fun isModified(): Boolean {
        return selectedSound != soundPlayer.getCurrentSoundFile() || selectedVolume != soundPlayer.getVolume()
    }

    override fun apply() {
        soundPlayer.reload(selectedSound, selectedVolume)
    }

    override fun getDisplayName(): String {
        return "Breakpoint Sound"
    }
}