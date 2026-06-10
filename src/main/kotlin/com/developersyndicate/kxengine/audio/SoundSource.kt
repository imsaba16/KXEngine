package com.developersyndicate.kxengine.audio

import org.lwjgl.openal.AL10.*

class SoundSource(val loop: Boolean = false) {
    var sourceId: Int = 0
        private set

    init {
        if (SoundEngine.isEnabled()) {
            sourceId = alGenSources()
            alSourcei(sourceId, AL_LOOPING, if (loop) AL_TRUE else AL_FALSE)
            alSourcef(sourceId, AL_GAIN, 1.0f)
            alSourcef(sourceId, AL_PITCH, 1.0f)
        }
    }

    fun play(sound: Sound) {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        stop()
        if (sound.bufferId != 0) {
            alSourcei(sourceId, AL_BUFFER, sound.bufferId)
            alSourcePlay(sourceId)
        }
    }

    fun stop() {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourceStop(sourceId)
    }

    fun pause() {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourcePause(sourceId)
    }

    fun setVolume(volume: Float) {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourcef(sourceId, AL_GAIN, volume.coerceIn(0f, 1f))
    }

    fun isPlaying(): Boolean {
        if (!SoundEngine.isEnabled() || sourceId == 0) return false
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING
    }

    fun destroy() {
        if (SoundEngine.isEnabled() && sourceId != 0) {
            stop()
            alDeleteSources(sourceId)
            sourceId = 0
        }
    }
}
