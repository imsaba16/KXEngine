package com.developersyndicate.kxengine.combat

class DamageSystem {

    fun apply(
        health: Health,
        damage: Damage
    ) {
        if (health.canTakeDamage()) {
            health.damage(damage.amount)
        }
    }
}