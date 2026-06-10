package com.developersyndicate.kxengine.audio

interface SoundSource {
    fun play(sound: Sound)
    fun stop()
    fun pause()
    fun setPosition(x: Float, y: Float, z: Float)
    fun setVolume(volume: Float)
    fun isPlaying(): Boolean
    fun destroy()
}
