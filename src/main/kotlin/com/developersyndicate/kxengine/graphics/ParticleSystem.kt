package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.batch.SpriteBatch

class ParticleSystem {
    private val emitters = mutableListOf<ParticleEmitter>()

    fun add(emitter: ParticleEmitter) {
        emitters.add(emitter)
    }

    fun remove(emitter: ParticleEmitter) {
        emitters.remove(emitter)
    }

    fun update(dt: Float) {
        val iterator = emitters.iterator()
        while (iterator.hasNext()) {
            val emitter = iterator.next()
            emitter.update(dt)
        }
    }

    fun draw(batch: SpriteBatch) {
        for (emitter in emitters) {
            emitter.draw(batch)
        }
    }
}
