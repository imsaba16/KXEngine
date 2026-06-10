package com.developersyndicate.kxengine.input

object Input {
    private val keys = BooleanArray(512)
    private val lastKeys = BooleanArray(512)

    var mouseX = 0f
        private set
    var mouseY = 0f
        private set

    private val mouseButtons = BooleanArray(8)
    private val lastMouseButtons = BooleanArray(8)

    fun isKeyDown(key: Int): Boolean = keys[key]

    fun isKeyPressed(key: Int): Boolean =
        keys[key] && !lastKeys[key]

    fun isMouseButtonDown(button: Int): Boolean =
        mouseButtons.getOrElse(button) { false }

    fun isMouseButtonPressed(button: Int): Boolean =
        mouseButtons.getOrElse(button) { false } && !lastMouseButtons.getOrElse(button) { false }

    internal fun setKey(key: Int, pressed: Boolean) {
        if (key in keys.indices) {
            keys[key] = pressed
        }
    }

    internal fun setMousePosition(x: Float, y: Float) {
        mouseX = x
        mouseY = y
    }

    internal fun setMouseButton(button: Int, pressed: Boolean) {
        if (button in mouseButtons.indices) {
            mouseButtons[button] = pressed
        }
    }

    fun endFrame() {
        for (i in keys.indices) {
            lastKeys[i] = keys[i]
        }
        for (i in mouseButtons.indices) {
            lastMouseButtons[i] = mouseButtons[i]
        }
    }
}