package com.developersyndicate.kxengine.input

object Input {
    private val keys = BooleanArray(512)
    private val lastKeys = BooleanArray(512)

    fun isKeyDown(key: Int): Boolean = keys[key]

    fun isKeyPressed(key: Int): Boolean =
        keys[key] && !lastKeys[key]

    internal fun setKey(key: Int, pressed: Boolean) {
        if (key in keys.indices) {
            keys[key] = pressed
        }
    }

    fun endFrame() {
        for (i in keys.indices) {
            lastKeys[i] = keys[i]
        }
    }
}