package bg.laskov.breakbeat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import kotlin.math.log10

@Service
class SoundPlayer {

    @Volatile
    private var clip: Clip? = null

    @Volatile
    private var loading = false

    private val settings = BreakpointSoundSettings.getInstance()

    fun play() {
        if (!settings.state.enabled) {
            return
        }
        val current = clip ?: return
        if (current.isRunning) {
            current.stop()
        }
        current.framePosition = 0

        val gainControl = current.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        val min = gainControl.minimum    // usually negative, e.g., -80 dB
        val max = gainControl.maximum    // usually 6 dB
        val volumeNormalized = settings.state.volume / 100f

        // Convert linear 0..1 volume to decibels
        val gain = if (volumeNormalized == 0f) {
            min
        } else {
            20 * log10(volumeNormalized.toDouble()).toFloat()
        }

        // Clamp to min/max
        gainControl.value = gain.coerceIn(min, max)
        current.start()
    }

    fun stop() {
        val current = clip ?: return
        if (current.isRunning) {
            current.stop()
        }
    }

    fun reload(soundFile: String, volume: Float) {

        val settings = BreakpointSoundSettings.getInstance()
        settings.state.selectedSoundPath = soundFile
        settings.state.volume = volume

        val oldClip = clip
        loadAsync(soundFile) {
            oldClip?.stop()
            oldClip?.close()
        }
    }

    fun reload(soundFile: String, volume: Float, enabled: Boolean) {
        this.reload(soundFile, volume)
        this.settings.state.enabled = enabled
    }

    private fun loadAsync(soundFile: String, onLoaded: (() -> Unit)? = null) {
        if (loading) return
        loading = true

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val resourcePath = "sounds/$soundFile.wav"
                val url = BreakpointSoundListener::class.java.classLoader.getResource(resourcePath)
                    ?: throw IllegalStateException("Sound resource not found: $resourcePath")
                val audioInputStream = AudioSystem.getAudioInputStream(url)
                val newClip: Clip = AudioSystem.getClip()
                newClip.open(audioInputStream)
                clip = newClip
                onLoaded?.invoke()
            } catch (_: Exception) {
                // swallow: sound failure should not break debugging
            } finally {
                loading = false
            }
        }
    }

    private fun dispose() {
        clip?.stop()
        clip?.close()
        clip = null
    }

    fun getCurrentSoundFile(): String {
        return settings.state.selectedSoundPath ?: ""
    }

    fun getVolume(): Float {
        return settings.state.volume
    }

    fun setEnabled(enabled: Boolean) {
        this.settings.state.enabled = enabled

    }
}