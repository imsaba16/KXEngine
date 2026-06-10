package com.developersyndicate.kxengine.audio

import org.lwjgl.openal.AL10.*
import javax.sound.sampled.AudioSystem
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Sound(resourcePath: String) {
    var bufferId: Int = 0
        private set

    init {
        loadSound(resourcePath)
    }

    private fun loadSound(resourcePath: String) {
        if (!SoundEngine.isEnabled()) return

        val stream = Sound::class.java.classLoader.getResourceAsStream(resourcePath)
        if (stream == null) {
            println("Warning: Sound file not found on classpath: $resourcePath. Playback will be silent.")
            return
        }

        val audioStream = AudioSystem.getAudioInputStream(BufferedInputStream(stream))
        val format = audioStream.format
        val bytes = audioStream.readAllBytes()
        audioStream.close()

        val buffer = ByteBuffer.allocateDirect(bytes.size)
            .order(ByteOrder.nativeOrder())
            .put(bytes)
            .flip() as ByteBuffer

        val alFormat = when {
            format.channels == 1 && format.sampleSizeInBits == 8 -> AL_FORMAT_MONO8
            format.channels == 1 && format.sampleSizeInBits == 16 -> AL_FORMAT_MONO16
            format.channels == 2 && format.sampleSizeInBits == 8 -> AL_FORMAT_STEREO8
            format.channels == 2 && format.sampleSizeInBits == 16 -> AL_FORMAT_STEREO16
            else -> error("Unsupported audio format: channels=${format.channels}, sampleSize=${format.sampleSizeInBits}")
        }

        bufferId = alGenBuffers()
        alBufferData(bufferId, alFormat, buffer, format.sampleRate.toInt())
    }

    fun destroy() {
        if (SoundEngine.isEnabled() && bufferId != 0) {
            alDeleteBuffers(bufferId)
            bufferId = 0
        }
    }
}
