package com.developersyndicate.kxengine.combat

import com.developersyndicate.kxengine.graphics.animation.Animator
import com.developersyndicate.kxengine.graphics.animation.SpriteAnimation
import com.developersyndicate.kxengine.physics.Body

class EnemyDeath(
    private val animator: Animator,
    private val deathAnimation: SpriteAnimation,
    private val body: Body,
    private val despawnTime: Float = 0.6f
) {
    var state = DeathState.ALIVE
        private set

    private var timer = 0f

    fun trigger() {
        if (state != DeathState.ALIVE) return

        state = DeathState.DYING
        timer = despawnTime

        body.velocity = body.velocity.copy(x = 0f, y = 0f)
        animator.play(deathAnimation)
    }

    fun update(delta: Float) {
        if (state != DeathState.DYING) return

        timer -= delta
        if (timer <= 0f) {
            state = DeathState.DEAD
        }
    }

    fun isDead(): Boolean = state == DeathState.DEAD
}