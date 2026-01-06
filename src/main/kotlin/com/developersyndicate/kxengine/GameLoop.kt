package com.developersyndicate.kxengine

class GameLoop(private val time: Time) {
    var running = false

    fun run(update: (Float) -> Unit) {
        running = true
        while (running) {
            time.update()
            update(time.deltaTime)
        }
    }

    fun stop() {
        running = false
    }
}
