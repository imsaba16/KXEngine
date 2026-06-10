package com.developersyndicate.kxengine.graphics.animation

import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

abstract class AnimationState(val name: String) {
    val transitions = mutableListOf<Transition>()

    abstract fun update(delta: Float, controller: AnimatorController)
    abstract fun currentFrame(controller: AnimatorController): AtlasRegion
    abstract fun reset()
    abstract fun isFinished(): Boolean
}

class ClipState(
    name: String,
    val animation: SpriteAnimation
) : AnimationState(name) {
    private var time = 0f

    override fun update(delta: Float, controller: AnimatorController) {
        time += delta
        val totalTime = animation.length
        if (animation.loop) {
            if (totalTime > 0f) {
                time %= totalTime
            }
        } else if (time > totalTime) {
            time = totalTime
        }
    }

    override fun currentFrame(controller: AnimatorController): AtlasRegion {
        val frameCount = animation.frames.size
        if (frameCount == 0) error("ClipState '$name' has no animation frames")
        val frameIndex = (time / animation.frameDuration).toInt().coerceIn(0, frameCount - 1)
        return animation.frames[frameIndex]
    }

    override fun reset() {
        time = 0f
    }

    override fun isFinished(): Boolean {
        if (animation.loop) return false
        return time >= animation.length
    }
}

class BlendTreeState(
    name: String,
    val parameterName: String
) : AnimationState(name) {
    class BlendChild(
        val threshold: Float,
        val animation: SpriteAnimation
    )

    private val children = mutableListOf<BlendChild>()
    private var time = 0f

    fun addSubAnimation(threshold: Float, animation: SpriteAnimation) {
        children.add(BlendChild(threshold, animation))
        children.sortBy { it.threshold }
    }

    override fun update(delta: Float, controller: AnimatorController) {
        val paramVal = (controller.getParameter(parameterName) as? Number)?.toFloat() ?: 0f
        val activeAnim = getActiveAnimation(paramVal) ?: return

        time += delta
        val totalTime = activeAnim.length
        if (activeAnim.loop) {
            if (totalTime > 0f) {
                time %= totalTime
            }
        } else if (time > totalTime) {
            time = totalTime
        }
    }

    override fun currentFrame(controller: AnimatorController): AtlasRegion {
        val paramVal = (controller.getParameter(parameterName) as? Number)?.toFloat() ?: 0f
        val activeAnim = getActiveAnimation(paramVal) ?: error("BlendTreeState '$name' has no sub-animations")

        val frameCount = activeAnim.frames.size
        if (frameCount == 0) error("BlendTreeState '$name' active animation has no frames")
        val frameIndex = (time / activeAnim.frameDuration).toInt().coerceIn(0, frameCount - 1)
        return activeAnim.frames[frameIndex]
    }

    private fun getActiveAnimation(value: Float): SpriteAnimation? {
        if (children.isEmpty()) return null
        if (children.size == 1) return children[0].animation

        if (value <= children.first().threshold) return children.first().animation
        if (value >= children.last().threshold) return children.last().animation

        for (i in 0 until children.size - 1) {
            val c1 = children[i]
            val c2 = children[i + 1]
            if (value >= c1.threshold && value <= c2.threshold) {
                return if (value - c1.threshold < c2.threshold - value) c1.animation else c2.animation
            }
        }
        return children.first().animation
    }

    override fun reset() {
        time = 0f
    }

    override fun isFinished(): Boolean {
        if (children.isEmpty()) return true
        val activeAnim = children.first().animation
        if (activeAnim.loop) return false
        return time >= activeAnim.length
    }
}
