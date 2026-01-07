package com.developersyndicate.kxengine.assets

import com.developersyndicate.kxengine.graphics.Texture

class TextureAsset(
    val path: String
) {
    val texture: Texture = Texture(path)

    fun dispose() {
        texture.destroy()
    }
}