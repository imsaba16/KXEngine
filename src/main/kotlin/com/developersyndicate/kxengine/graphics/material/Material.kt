package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.Shader

interface Material {
    fun bind(shader: Shader)
}