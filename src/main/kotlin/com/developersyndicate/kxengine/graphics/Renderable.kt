package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.material.Material

class Renderable(
    val mesh: QuadMesh,
    val transform: Transform,
    val material: Material
)