package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.graphics.Renderable
import com.developersyndicate.kxengine.graphics.Mesh
import com.developersyndicate.kxengine.graphics.material.Material

class SpriteNode(
    name: String = "SpriteNode",
    val mesh: Mesh,
    var material: Material,
    var zIndex: Int = 0
) : Node(name) {
    val renderable = Renderable(mesh, transform, material, null, zIndex)
}
