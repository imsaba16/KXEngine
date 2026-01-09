package com.developersyndicate.kxengine.ecs

interface System {
    fun update(world: World, delta: Float)
}