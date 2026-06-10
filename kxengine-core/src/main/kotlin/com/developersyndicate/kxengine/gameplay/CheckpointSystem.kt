package com.developersyndicate.kxengine.gameplay

import com.developersyndicate.kxengine.combat.Health
import com.developersyndicate.kxengine.graphics.Transform
import com.developersyndicate.kxengine.math.Vec3
import com.developersyndicate.kxengine.physics.Body

class CheckpointSystem(
    private val defaultSpawn: Vec3 = Vec3.ZERO
) {

    private var active: Checkpoint? = null

    fun setCheckpoint(checkpoint: Checkpoint) {
        active = checkpoint
        println("Checkpoint activated at ${checkpoint.position}")
    }

    fun respawn(
        transform: Transform,
        body: Body,
        health: Health
    ) {
        val cpPos = active?.position ?: defaultSpawn

        transform.position = cpPos
        body.velocity = body.velocity.copy(x = 0f, y = 0f)
        body.grounded = false
        health.current = health.max

        println("Respawned at checkpoint/default spawn")
    }
}