package com.developersyndicate.kxengine.audio

interface IAudioEngine {
    fun init()
    fun isEnabled(): Boolean
    fun loadSound(path: String): Sound
    fun createSoundSource(loop: Boolean = false): SoundSource
    fun destroy()
}
