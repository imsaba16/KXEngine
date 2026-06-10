package com.developersyndicate.kxengine.audio

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.AL10.*
import java.nio.IntBuffer

class LwjglAudioEngine : IAudioEngine {
    private var device: Long = 0L
    private var context: Long = 0L

    override fun init() {
        device = alcOpenDevice(null as CharSequence?)
        if (device == 0L) {
            println("Warning: Failed to open default OpenAL device. Audio will be disabled.")
            return
        }

        val deviceCaps = ALC.createCapabilities(device)
        context = alcCreateContext(device, null as IntBuffer?)
        if (context == 0L) {
            println("Warning: Failed to create OpenAL context. Audio will be disabled.")
            alcCloseDevice(device)
            device = 0L
            return
        }

        alcMakeContextCurrent(context)
        AL.createCapabilities(deviceCaps)

        alListener3f(AL_POSITION, 0f, 0f, 0f)
        alListener3f(AL_VELOCITY, 0f, 0f, 0f)

        println("SoundEngine: OpenAL initialized successfully.")
    }

    override fun isEnabled(): Boolean = device != 0L && context != 0L

    override fun loadSound(path: String): Sound {
        return LwjglSound(path)
    }

    override fun createSoundSource(loop: Boolean): SoundSource {
        return LwjglSoundSource(loop)
    }

    override fun destroy() {
        if (!isEnabled()) return
        
        alcMakeContextCurrent(0L)
        alcDestroyContext(context)
        alcCloseDevice(device)
        device = 0L
        context = 0L
        println("SoundEngine: OpenAL destroyed.")
    }
}
