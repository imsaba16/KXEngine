package com.developersyndicate.kxengine.audio

object SoundEngine {
    lateinit var delegate: IAudioEngine

    fun init() {
        delegate.init()
    }

    fun isEnabled(): Boolean = delegate.isEnabled()

    fun destroy() {
        delegate.destroy()
    }
}
