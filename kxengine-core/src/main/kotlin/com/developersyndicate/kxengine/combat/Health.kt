package com.developersyndicate.kxengine.combat

class Health(
    val max: Int,
    var current: Int = max,
    private val invincibleTime: Float = 0.5f
) {
    private var invincibleTimer = 0f

    val isAlive: Boolean
        get() = current > 0

    fun update(delta: Float) {
        if (invincibleTimer > 0f) {
            invincibleTimer -= delta
        }
    }

    fun canTakeDamage(): Boolean = invincibleTimer <= 0f

    fun damage(amount: Int) {
        if (!canTakeDamage()) return

        current -= amount
        invincibleTimer = invincibleTime

        if (current < 0) current = 0
    }
}