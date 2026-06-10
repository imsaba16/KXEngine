package com.developersyndicate.kxengine.assets

import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.platform.KXPlatform

class TextureAsset(
    val path: String
) {
    val texture: Texture = KXPlatform.graphicsFactory.createTexture(path)

    fun dispose() {
        texture.destroy()
    }
}