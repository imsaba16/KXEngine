package com.developersyndicate.kxengine.audio

import com.developersyndicate.kxengine.platform.KXPlatform
import org.lwjgl.openal.AL10.*
import javax.sound.sampled.AudioSystem
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class LwjglSound(resourcePath: String) : Sound {
    var bufferId: Int = 0
        private set

    init {
        loadSound(resourcePath)
    }

    private fun loadSound(resourcePath: String) {
        if (!SoundEngine.isEnabled()) return

        if (!KXPlatform.fileSystem.exists(resourcePath)) {
            println("Warning: Audio file not found: $resourcePath. Sound will be silent.")
            return
        }

        try {
            val bytes = KXPlatform.fileSystem.readBytes(resourcePath)
            val stream = bytes.inputStream()

            val audioStream = AudioSystem.getAudioInputStream(BufferedInputStream(stream))
            val format = audioStream.format
            val streamBytes = audioStream.readAllBytes()
            audioStream.close()

            val buffer = ByteBuffer.allocateDirect(streamBytes.size)
                .order(ByteOrder.nativeOrder())
                .put(streamBytes)
                .flip() as ByteBuffer

            val alFormat = when {
                format.channels == 1 && format.sampleSizeInBits == 8 -> AL_FORMAT_MONO8
                format.channels == 1 && format.sampleSizeInBits == 16 -> AL_FORMAT_MONO16
                format.channels == 2 && format.sampleSizeInBits == 8 -> AL_FORMAT_STEREO8
                format.channels == 2 && format.sampleSizeInBits == 16 -> AL_FORMAT_STEREO16
                else -> {
                    println("Warning: Unsupported audio format: channels=${format.channels}, sampleSize=${format.sampleSizeInBits} for $resourcePath. Sound will be silent.")
                    return
                }
            }

            bufferId = alGenBuffers()
            alBufferData(bufferId, alFormat, buffer, format.sampleRate.toInt())
        } catch (e: Exception) {
            println("Warning: Failed to load sound $resourcePath: ${e.message}. Sound will be silent.")
        }
    }

    override fun destroy() {
        if (SoundEngine.isEnabled() && bufferId != 0) {
            alDeleteBuffers(bufferId)
            bufferId = 0
        }
    }
}
