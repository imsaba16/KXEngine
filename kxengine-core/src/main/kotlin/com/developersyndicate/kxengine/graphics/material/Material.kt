package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.shader.Shader

interface Material {
    fun bind(shader: Shader)
}