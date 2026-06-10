package com.developersyndicate.kxengine.combat

import com.developersyndicate.kxengine.physics.Collider

class AttackSystem(
    private val cooldown: Float = 0.4f
) {
    private var cooldownTimer = 0f
    private val activeAttacks = mutableListOf<Attack>()

    fun update(delta: Float) {
        if (cooldownTimer > 0f) cooldownTimer -= delta

        activeAttacks.removeIf {
            it.lifetime -= delta
            it.lifetime <= 0f
        }
    }

    fun canAttack(): Boolean = cooldownTimer <= 0f

    fun spawnAttack(attack: Attack) {
        if (!canAttack()) return
        cooldownTimer = cooldown
        activeAttacks.add(attack)
    }

    fun checkHits(
        enemyCollider: Collider,
        onHit: (Attack) -> Unit
    ) {
        for (attack in activeAttacks) {
            if (attack.collider.aabb().overlaps(enemyCollider.aabb())) {
                onHit(attack)
                attack.lifetime = 0f
            }
        }
    }
}