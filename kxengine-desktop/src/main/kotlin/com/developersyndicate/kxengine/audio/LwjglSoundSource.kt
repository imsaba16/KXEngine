package com.developersyndicate.kxengine.audio

import org.lwjgl.openal.AL10.*

class LwjglSoundSource(val loop: Boolean = false) : SoundSource {
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

    override fun play(sound: Sound) {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        stop()
        val lwjglSound = sound as? LwjglSound
        if (lwjglSound != null && lwjglSound.bufferId != 0) {
            alSourcei(sourceId, AL_BUFFER, lwjglSound.bufferId)
            alSourcePlay(sourceId)
        }
    }

    override fun stop() {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourceStop(sourceId)
    }

    override fun pause() {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourcePause(sourceId)
    }

    override fun setPosition(x: Float, y: Float, z: Float) {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSource3f(sourceId, AL_POSITION, x, y, z)
    }

    override fun setVolume(volume: Float) {
        if (!SoundEngine.isEnabled() || sourceId == 0) return
        alSourcef(sourceId, AL_GAIN, volume.coerceIn(0f, 1f))
    }

    override fun isPlaying(): Boolean {
        if (!SoundEngine.isEnabled() || sourceId == 0) return false
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING
    }

    override fun destroy() {
        if (SoundEngine.isEnabled() && sourceId != 0) {
            stop()
            alDeleteSources(sourceId)
            sourceId = 0
        }
    }
}
