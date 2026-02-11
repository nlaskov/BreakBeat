package bg.laskov.breakbeat.ui

import bg.laskov.breakbeat.BreakpointSoundState
import bg.laskov.breakbeat.SoundPlayer
import bg.laskov.breakbeat.enums.BreakpointControlMode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JRadioButton
import kotlin.math.abs

class BreakpointSoundConfigurable : Configurable {

    private val settings = BreakpointSoundState.getInstance()
    private val soundPlayer =
        ApplicationManager.getApplication().getService(SoundPlayer::class.java)

    private var panel: JPanel? = null
    private lateinit var soundComboBox: ComboBox<String>
    private lateinit var enabledToggle: JBCheckBox
    private lateinit var volumePresetComboBox: ComboBox<String>
    private lateinit var testButton: JButton
    private val allRadio = JRadioButton("ALL")
    private val noneRadio = JRadioButton("NONE")
    private val customRadio = JRadioButton("CUSTOM")
    private val breakpointControlPanel = JPanel()

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
    private var selectedBreakpointControl: BreakpointControlMode = BreakpointControlMode.ALL // ALL / NONE / CUSTOM

    override fun createComponent(): JComponent? {
        // Load the current selection from persisted settings
        selectedSound = settings.state.selectedSoundPath
        selectedVolume = settings.state.volume
        selectedEnabled = settings.state.enabled
        selectedBreakpointControl = settings.state.breakpointControlMode


        // Choose sound
        soundComboBox = ComboBox(sounds.toTypedArray())
        soundComboBox.selectedItem = selectedSound
        soundComboBox.addActionListener {
            selectedSound = soundComboBox.selectedItem as String
        }

        // Enabled toggle
        enabledToggle = JBCheckBox("Enable breakpoint sound", selectedEnabled)

        // Breakpoint Control (ALL / NONE / CUSTOM)
        breakpointControlPanel.layout = BoxLayout(breakpointControlPanel, BoxLayout.Y_AXIS)

        // Needed for radio button grouping (allows only one to be selected at a time)
        val group = ButtonGroup()
        group.add(allRadio)
        group.add(noneRadio)
        group.add(customRadio)

        when (selectedBreakpointControl) {
            BreakpointControlMode.NONE -> noneRadio.isSelected = true
            BreakpointControlMode.CUSTOM -> customRadio.isSelected = true
            else -> allRadio.isSelected = true
        }

        allRadio.addActionListener { updateSelectedControl(BreakpointControlMode.ALL) }
        customRadio.addActionListener { updateSelectedControl(BreakpointControlMode.CUSTOM) }
        noneRadio.addActionListener { updateSelectedControl(BreakpointControlMode.NONE) }

        // Respect global enable toggle (optional, keeps UI consistent)
        allRadio.isEnabled = selectedEnabled
        noneRadio.isEnabled = selectedEnabled
        customRadio.isEnabled = selectedEnabled

        breakpointControlPanel.add(allRadio)
        breakpointControlPanel.add(customRadio)
        breakpointControlPanel.add(noneRadio)

        enabledToggle.addItemListener {
            selectedEnabled = enabledToggle.isSelected
            if (this::soundComboBox.isInitialized) soundComboBox.isEnabled = selectedEnabled
            if (this::volumePresetComboBox.isInitialized) volumePresetComboBox.isEnabled = selectedEnabled
            if (this::testButton.isInitialized) testButton.isEnabled = selectedEnabled
            allRadio.isEnabled = selectedEnabled
            noneRadio.isEnabled = selectedEnabled
            customRadio.isEnabled = selectedEnabled
        }

        // Volume preset
        volumePresetComboBox = ComboBox(volumePresets.map { it.first }.toTypedArray())

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
            .addSeparator()
            .addLabeledComponent("Breakpoint control", breakpointControlPanel) // radio buttons panel
            .addSeparator()
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
                selectedEnabled != settings.state.enabled ||
                selectedBreakpointControl != settings.state.breakpointControlMode
    }

    override fun apply() {
        soundPlayer.reload(selectedSound, selectedVolume, selectedEnabled)

        settings.state.selectedSoundPath = selectedSound
        settings.state.volume = selectedVolume
        settings.state.enabled = selectedEnabled
        settings.state.breakpointControlMode = selectedBreakpointControl

        ProjectManager.getInstance().openProjects.forEach { project ->
            WindowManager.getInstance().getStatusBar(project)?.updateWidget("BreakpointSoundStatus")
        }
    }

    override fun reset() {
        selectedSound = settings.state.selectedSoundPath
        selectedVolume = settings.state.volume
        selectedEnabled = settings.state.enabled
        selectedBreakpointControl = settings.state.breakpointControlMode

        if (this::soundComboBox.isInitialized) {
            soundComboBox.selectedItem = selectedSound
        }
        if (this::volumePresetComboBox.isInitialized) {
            val closest = volumePresets.minBy { abs(it.second - selectedVolume) }.first
            volumePresetComboBox.selectedItem = closest
            volumePresetComboBox.isEnabled = selectedEnabled
        }
        if (this::enabledToggle.isInitialized) {
            enabledToggle.isSelected = selectedEnabled
        }

        this.noneRadio.isEnabled = selectedEnabled
        this.customRadio.isEnabled = selectedEnabled
        this.allRadio.isEnabled = selectedEnabled

        when (selectedBreakpointControl) {
            BreakpointControlMode.NONE -> noneRadio.isSelected = true
            BreakpointControlMode.CUSTOM -> customRadio.isSelected = true
            else -> allRadio.isSelected = true
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

    fun updateSelectedControl(value: BreakpointControlMode) {
        selectedBreakpointControl = value
    }

    fun closestPresetLabel(volume: Float): String {
        return volumePresets.minBy { abs(it.second - volume) }.first
    }
}