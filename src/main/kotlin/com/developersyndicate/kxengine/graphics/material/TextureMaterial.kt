package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.Shader
import com.developersyndicate.kxengine.graphics.Texture

class TextureMaterial(
    val texture: Texture
) : Material {

    override fun bind(shader: Shader) {
        texture.bind(0)
        shader.setInt("uTexture", 0)
    }
}
