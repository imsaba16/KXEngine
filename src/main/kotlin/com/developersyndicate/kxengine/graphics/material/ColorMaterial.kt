package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.Shader

class ColorMaterial(
    val color: Color
) : Material {

    override fun bind(shader: Shader) {
        shader.setVec4("uColor", color)
    }
}