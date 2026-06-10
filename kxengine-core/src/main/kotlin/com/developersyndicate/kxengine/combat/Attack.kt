package com.developersyndicate.kxengine.combat

import com.developersyndicate.kxengine.physics.Collider

class Attack(
    val collider: Collider,
    val damage: Int,
    val knockback: Knockback,
    var lifetime: Float
)