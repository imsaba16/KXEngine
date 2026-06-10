package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

class TextureMaterial(
    val texture: Texture,
    var region: AtlasRegion,
    var normalMap: Texture? = null
) : Material {

    override fun bind(shader: Shader) {
        texture.bind(0)
        shader.setInt("uTexture", 0)
        
        val normal = normalMap
        if (normal != null) {
            normal.bind(1)
            shader.setInt("uNormalMap", 1)
            shader.setInt("uUseNormalMap", 1)
        } else {
            shader.setInt("uUseNormalMap", 0)
        }
    }
}
