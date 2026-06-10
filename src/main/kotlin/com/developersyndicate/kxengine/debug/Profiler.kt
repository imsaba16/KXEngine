package com.developersyndicate.kxengine.debug

import com.developersyndicate.kxengine.EngineConfig

object Profiler {
    var drawCalls = 0
    private var frameCount = 0
    private var lastTime = System.currentTimeMillis()
    private var fps = 0

    fun tickFrame() {
        frameCount++
        val now = System.currentTimeMillis()
        if (now - lastTime >= 1000) {
            fps = frameCount
            frameCount = 0
            lastTime = now

            val runtime = Runtime.getRuntime()
            val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

            if (EngineConfig.profilerEnabled) {
                println("SoundEngine/Profiler: FPS: $fps | JVM Heap: ${usedMem}MB | Draw Calls/Frame: $drawCalls")
            }
        }
        drawCalls = 0
    }
}
