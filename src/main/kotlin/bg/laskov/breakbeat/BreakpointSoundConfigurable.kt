package bg.laskov.breakbeat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import javax.swing.*
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.ui.FormBuilder
import com.intellij.ui.components.JBCheckBox
import java.awt.BorderLayout
import java.awt.FlowLayout

class BreakpointSoundConfigurable : Configurable {

    private val settings = BreakpointSoundSettings.getInstance()
    private val soundPlayer =
        ApplicationManager.getApplication().getService(SoundPlayer::class.java)

    private var panel: JPanel? = null
    private lateinit var soundComboBox: ComboBox<String>
    private lateinit var enabledToggle: JBCheckBox
    private lateinit var volumePresetComboBox: ComboBox<String>
    private lateinit var testButton: JButton

    // Example list of sounds
    private val sounds = listOf("beep", "cow", "chicken")

    private val volumePresets: List<Pair<String, Float>> = listOf(
        "Low" to 25f,
        "Medium" to 50f,
        "High" to 75f,
        "Max" to 100f,
    )

    private var selectedSound: String = settings.state.selectedSoundPath
    private var selectedVolume: Float = settings.state.volume
    private var selectedEnabled: Boolean = settings.state.enabled

    override fun createComponent(): JComponent? {
        // Load the current selection from persisted settings
        selectedSound = settings.state.selectedSoundPath
        selectedVolume = settings.state.volume
        selectedEnabled = settings.state.enabled


        // Choose sound
        soundComboBox = ComboBox(sounds.toTypedArray())
        soundComboBox.selectedItem = selectedSound
        soundComboBox.addActionListener {
            selectedSound = soundComboBox.selectedItem as String
        }

        // Enabled toggle
        enabledToggle = JBCheckBox("Enable breakpoint sound", selectedEnabled)
        enabledToggle.addItemListener {
            selectedEnabled = enabledToggle.isSelected
            if (this::soundComboBox.isInitialized) soundComboBox.isEnabled = selectedEnabled
            if (this::volumePresetComboBox.isInitialized) volumePresetComboBox.isEnabled = selectedEnabled
            if (this::testButton.isInitialized) testButton.isEnabled = selectedEnabled
        }

        // Volume preset
        volumePresetComboBox = ComboBox(volumePresets.map { it.first }.toTypedArray())

        fun closestPresetLabel(volume: Float): String {
            return volumePresets.minBy { kotlin.math.abs(it.second - volume) }.first
        }

        volumePresetComboBox.selectedItem = closestPresetLabel(selectedVolume)
        volumePresetComboBox.addActionListener {
            val label = volumePresetComboBox.selectedItem as? String ?: return@addActionListener
            selectedVolume = volumePresets.first { it.first == label }.second
        }

        volumePresetComboBox.isEnabled = selectedEnabled

        // Test button
        testButton = JButton("Play preview")
        testButton.addActionListener {
            // Play a preview using the currently selected values (without persisting settings)
            soundPlayer.playTest(selectedSound, selectedVolume)
        }
        testButton.isEnabled = selectedEnabled
        soundComboBox.isEnabled = selectedEnabled
        volumePresetComboBox.isEnabled = selectedEnabled

        val previewRow = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0))
        previewRow.add(testButton)

        val formPanel = FormBuilder.createFormBuilder()
            .addComponent(enabledToggle)
            .addLabeledComponent("Sound", soundComboBox)
            .addLabeledComponent("Volume", volumePresetComboBox)
            .addComponent(previewRow)
            .panel

        panel = JPanel(BorderLayout())
        panel!!.add(formPanel, BorderLayout.NORTH)

        return panel
    }

    override fun isModified(): Boolean {
        return selectedSound != settings.state.selectedSoundPath ||
            selectedVolume != settings.state.volume ||
            selectedEnabled != settings.state.enabled
    }

    override fun apply() {
        soundPlayer.reload(selectedSound, selectedVolume, selectedEnabled)

        ProjectManager.getInstance().openProjects.forEach { project ->
            WindowManager.getInstance().getStatusBar(project)?.updateWidget("BreakpointSoundStatus")
        }
    }

    override fun reset() {
        selectedSound = settings.state.selectedSoundPath
        selectedVolume = settings.state.volume
        selectedEnabled = settings.state.enabled

        if (this::soundComboBox.isInitialized) {
            soundComboBox.selectedItem = selectedSound
        }
        if (this::volumePresetComboBox.isInitialized) {
            val closest = volumePresets.minBy { kotlin.math.abs(it.second - selectedVolume) }.first
            volumePresetComboBox.selectedItem = closest
            volumePresetComboBox.isEnabled = selectedEnabled
        }
        if (this::enabledToggle.isInitialized) {
            enabledToggle.isSelected = selectedEnabled
        }
        if (this::soundComboBox.isInitialized) {
            soundComboBox.isEnabled = selectedEnabled
        }
        if (this::volumePresetComboBox.isInitialized) {
            volumePresetComboBox.isEnabled = selectedEnabled
        }
        if (this::testButton.isInitialized) {
            testButton.isEnabled = selectedEnabled
        }
    }

    override fun getDisplayName(): String {
        return "Breakpoint Sound"
    }
}