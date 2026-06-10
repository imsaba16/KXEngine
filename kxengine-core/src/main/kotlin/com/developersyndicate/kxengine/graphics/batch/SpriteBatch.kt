package com.developersyndicate.kxengine.graphics.batch

import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.math.Mat4

enum class BlendMode {
    ALPHA, ADDITIVE
}

interface SpriteBatch {
    fun begin(texture: Texture)
    fun switchTexture(texture: Texture)
    fun draw(model: Mat4, region: AtlasRegion, color: FloatArray)
    fun draw(
        x: Float,
        y: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float,
        region: AtlasRegion,
        color: FloatArray
    )
    fun setBlendMode(blendMode: BlendMode)
    fun end()
    fun destroy()
}
