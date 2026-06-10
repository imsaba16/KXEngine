package com.developersyndicate.kxengine.input

import com.developersyndicate.kxengine.platform.PlatformKey

class GlfwInput : IInput {
    private val keys = BooleanArray(512)
    private val lastKeys = BooleanArray(512)

    override var touchX = 0f
        private set
    override var touchY = 0f
        private set
    override var isTouching = false
        private set

    fun setKey(key: Int, pressed: Boolean) {
        if (key in keys.indices) {
            keys[key] = pressed
        }
    }

    fun setMousePosition(x: Float, y: Float) {
        touchX = x
        touchY = y
    }

    fun setMouseButton(button: Int, pressed: Boolean) {
        if (button == 0) { // GLFW_MOUSE_BUTTON_LEFT
            isTouching = pressed
        }
    }

    override fun isKeyDown(key: PlatformKey): Boolean {
        val code = getGlfwKeyCode(key)
        return if (code != -1) keys[code] else false
    }

    override fun isKeyPressed(key: PlatformKey): Boolean {
        val code = getGlfwKeyCode(key)
        return if (code != -1) keys[code] && !lastKeys[code] else false
    }

    override fun endFrame() {
        for (i in keys.indices) {
            lastKeys[i] = keys[i]
        }
    }

    private fun getGlfwKeyCode(key: PlatformKey): Int {
        return when (key) {
            PlatformKey.UP -> 87      // GLFW_KEY_W
            PlatformKey.DOWN -> 83    // GLFW_KEY_S
            PlatformKey.LEFT -> 65    // GLFW_KEY_A
            PlatformKey.RIGHT -> 68   // GLFW_KEY_D
            PlatformKey.ACTION_A -> 74 // GLFW_KEY_J
            PlatformKey.ACTION_B -> 75 // GLFW_KEY_K
            PlatformKey.PAUSE -> 256  // GLFW_KEY_ESCAPE
            PlatformKey.START -> 257  // GLFW_KEY_ENTER
        }
    }
}
