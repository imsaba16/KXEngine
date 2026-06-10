package com.developersyndicate.kxengine.graphics.animation

import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

class Animator {

    private var currentAnimation: SpriteAnimation? = null
    private var time = 0f
    private var frameIndex = 0

    fun play(animation: SpriteAnimation, restart: Boolean = false) {
        if (animation == currentAnimation && !restart) return

        currentAnimation = animation
        time = 0f
        frameIndex = 0
    }

    fun update(delta: Float) {
        val anim = currentAnimation ?: return

        time += delta

        val frameCount = anim.frames.size
        val totalTime = anim.frameDuration * frameCount

        if (anim.loop) {
            time %= totalTime
        } else if (time >= totalTime) {
            time = totalTime - anim.frameDuration
        }

        frameIndex = (time / anim.frameDuration).toInt()
            .coerceIn(0, frameCount - 1)
    }

    fun currentFrame(): AtlasRegion {
        return currentAnimation
            ?.frames
            ?.get(frameIndex)
            ?: error("Animator has no animation playing")
    }
}