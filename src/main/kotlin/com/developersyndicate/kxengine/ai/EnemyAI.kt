package com.developersyndicate.kxengine.ai

import com.developersyndicate.kxengine.math.Vec2
import com.developersyndicate.kxengine.math.Vec3
import com.developersyndicate.kxengine.physics.Body
import com.developersyndicate.kxengine.graphics.Transform
import kotlin.math.abs

class EnemyAI(
    private val transform: Transform,
    private val body: Body,
    patrolPoints: List<Vec3>,
    private val patrolSpeed: Float = 1.5f,
    private val chaseSpeed: Float = 3.0f,
    private val detectionRange: Float = 3.0f,
    private val loseRange: Float = 4.5f
) {
    private val points = patrolPoints
    private var index = 0
    private var state = EnemyState.PATROL

    fun update(
        delta: Float,
        playerPos: Vec3
    ) {
        val distanceToPlayer =
            abs(playerPos.x - transform.position.x)

        // --- STATE SWITCH ---
        state = when (state) {
            EnemyState.PATROL ->
                if (distanceToPlayer < detectionRange)
                    EnemyState.CHASE
                else EnemyState.PATROL

            EnemyState.CHASE ->
                if (distanceToPlayer > loseRange)
                    EnemyState.PATROL
                else EnemyState.CHASE
        }

        when (state) {
            EnemyState.PATROL -> patrol()
            EnemyState.CHASE -> chase(playerPos)
        }
    }

    private fun patrol() {
        val target = points[index]
        val dir = if (target.x > transform.position.x) 1f else -1f

        body.velocity = body.velocity.copy(x = dir * patrolSpeed)

        if (abs(transform.position.x - target.x) < 0.1f) {
            index = (index + 1) % points.size
        }
    }

    private fun chase(playerPos: Vec3) {
        val dir =
            if (playerPos.x > transform.position.x) 1f else -1f

        body.velocity = body.velocity.copy(x = dir * chaseSpeed)
    }
}