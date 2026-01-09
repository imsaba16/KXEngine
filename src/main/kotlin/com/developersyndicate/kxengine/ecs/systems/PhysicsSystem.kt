package com.developersyndicate.kxengine.ecs.systems

import com.developersyndicate.kxengine.ecs.System
import com.developersyndicate.kxengine.ecs.World
import com.developersyndicate.kxengine.ecs.components.BodyC
import com.developersyndicate.kxengine.ecs.components.TransformC
import com.developersyndicate.kxengine.math.Vec3

class PhysicsSystem(
    private val gravity: Float
) : System {

    override fun update(world: World, delta: Float) {
        world.forEach(TransformC::class.java, BodyC::class.java) {
                _, transform, body ->

            body.velocity = body.velocity.copy(
                y = body.velocity.y + gravity * delta
            )

            transform.position = transform.position.copy(
                x = transform.position.x + body.velocity.x * delta,
                y = transform.position.y + body.velocity.y * delta
            )
        }
    }
}