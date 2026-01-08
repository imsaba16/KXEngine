package com.developersyndicate.kxengine.combat

import com.developersyndicate.kxengine.physics.Body

class DamageSystem {

    fun apply(
        health: Health,
        body: Body,
        damage: Damage,
        knockback: Knockback? = null
    ) {
        if (!health.canTakeDamage()) return

        health.damage(damage.amount)

        if (knockback != null) {
            body.velocity += knockback.force
        }
    }
}