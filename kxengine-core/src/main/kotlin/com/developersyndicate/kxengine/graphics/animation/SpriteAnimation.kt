package com.developersyndicate.kxengine.graphics.animation

import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

class SpriteAnimation(
    val frames: List<AtlasRegion>,
    val frameDuration: Float,
    val loop: Boolean = true
) {
    val length: Float = frames.size * frameDuration
}