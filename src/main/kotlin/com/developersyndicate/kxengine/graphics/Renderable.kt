package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.material.Material
import com.developersyndicate.kxengine.physics.Collider

class Renderable(
    val mesh: QuadMesh,
    val transform: Transform,
    val material: Material,
    val collider: Collider? = null
)