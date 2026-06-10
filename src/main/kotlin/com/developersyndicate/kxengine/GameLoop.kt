package com.developersyndicate.kxengine

class GameLoop(private val time: Time, val fixedDelta: Float = 1f / 60f) {
    var running = false

    fun run(updateFixed: (Float) -> Unit, updateVariable: (Float) -> Unit) {
        running = true
        var accumulator = 0f
        while (running) {
            time.update()
            val dt = time.deltaTime.coerceAtMost(0.25f)
            accumulator += dt
            while (accumulator >= fixedDelta) {
                updateFixed(fixedDelta)
                accumulator -= fixedDelta
            }
            updateVariable(dt)
        }
    }

    fun stop() {
        running = false
    }
}
