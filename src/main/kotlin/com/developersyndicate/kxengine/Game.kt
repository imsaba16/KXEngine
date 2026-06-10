package com.developersyndicate.kxengine

interface Game {
    fun init(engine: KXEngine)
    fun update(fixedDelta: Float)
    fun render(delta: Float)
    fun dispose()
}
