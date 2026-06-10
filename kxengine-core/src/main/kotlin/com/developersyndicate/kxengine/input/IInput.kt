package com.developersyndicate.kxengine.input

import com.developersyndicate.kxengine.platform.PlatformKey

interface IInput {
    fun isKeyDown(key: PlatformKey): Boolean
    fun isKeyPressed(key: PlatformKey): Boolean
    val touchX: Float
    val touchY: Float
    val isTouching: Boolean
    fun endFrame() {}
}
