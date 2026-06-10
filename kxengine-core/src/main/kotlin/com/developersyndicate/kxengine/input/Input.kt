package com.developersyndicate.kxengine.input

import com.developersyndicate.kxengine.platform.PlatformKey

object Input {
    lateinit var delegate: IInput

    fun isKeyDown(key: PlatformKey): Boolean = delegate.isKeyDown(key)
    fun isKeyPressed(key: PlatformKey): Boolean = delegate.isKeyPressed(key)
    val touchX: Float get() = delegate.touchX
    val touchY: Float get() = delegate.touchY
    val isTouching: Boolean get() = delegate.isTouching

    fun endFrame() {
        delegate.endFrame()
    }
}
