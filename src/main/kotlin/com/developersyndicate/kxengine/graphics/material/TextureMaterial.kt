package com.developersyndicate.kxengine.graphics.material

import com.developersyndicate.kxengine.graphics.Shader
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

class TextureMaterial(
    val texture: Texture,
    var region: AtlasRegion
) : Material {

    override fun bind(shader: Shader) {
        texture.bind(0)
        shader.setInt("uTexture", 0)
    }
}
