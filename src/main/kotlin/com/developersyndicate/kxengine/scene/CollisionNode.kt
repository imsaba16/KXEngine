package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.math.Vec2

class CollisionNode(
    name: String = "CollisionNode",
    val halfSize: Vec2,
    val collisionLayer: Int = 1,
    val collisionMask: Int = -1
) : Node(name) {
    val collider = Collider(transform, halfSize, collisionLayer, collisionMask)
}
