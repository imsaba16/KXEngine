package com.developersyndicate.kxengine.input

import com.developersyndicate.kxengine.platform.PlatformKey
import java.util.concurrent.ConcurrentHashMap

class AndroidInput : IInput {
    private val keysDown = ConcurrentHashMap<PlatformKey, Boolean>()
    private val keysPressed = ConcurrentHashMap<PlatformKey, Boolean>()

    @field:Volatile
    override var touchX: Float = 0f
        private set
        
    @field:Volatile
    override var touchY: Float = 0f
        private set
        
    @field:Volatile
    override var isTouching: Boolean = false
        private set

    override fun isKeyDown(key: PlatformKey): Boolean {
        return keysDown[key] ?: false
    }

    override fun isKeyPressed(key: PlatformKey): Boolean {
        return keysPressed[key] ?: false
    }

    override fun endFrame() {
        keysPressed.clear()
    }

    fun setKeyState(key: PlatformKey, isDown: Boolean) {
        val wasDown = keysDown[key] ?: false
        keysDown[key] = isDown
        if (isDown && !wasDown) {
            keysPressed[key] = true
        }
    }

    fun setTouchState(x: Float, y: Float, touching: Boolean) {
        touchX = x
        touchY = y
        isTouching = touching
    }
}
