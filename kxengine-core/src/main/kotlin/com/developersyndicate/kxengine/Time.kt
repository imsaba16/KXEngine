package com.developersyndicate.kxengine

class Time {
    var deltaTime = 0f
        private set

    private var lastTime = System.nanoTime()

    fun update() {
        val now = System.nanoTime()
        deltaTime = (now - lastTime) / 1_000_000_000f
        lastTime = now
    }
}
