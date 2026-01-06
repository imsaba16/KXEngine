package com.developersyndicate.kxengine.graphics

class Renderable(
    val mesh: TriangleMesh,
    val transform: Transform,
    var color: Color = Color.WHITE
)