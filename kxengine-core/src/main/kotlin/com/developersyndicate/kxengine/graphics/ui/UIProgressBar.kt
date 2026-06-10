package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch

class UIProgressBar(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    var progress: Float = 1.0f,
    var backgroundColor: Color = Color(0.2f, 0f, 0f, 1f),
    var foregroundColor: Color = Color(0.8f, 0.2f, 0.2f, 1f)
) : UIComponent(x, y, width, height) {

    private val whiteTexture = com.developersyndicate.kxengine.platform.KXPlatform.graphicsFactory.createTexture(1, 1, Color.WHITE)
    private val whiteRegion = AtlasRegion(0f, 0f, 1f, 1f)

    override fun update(
        dt: Float,
        mouseX: Float,
        mouseY: Float,
        isMouseDown: Boolean,
        wasMouseDown: Boolean
    ) {
        // Progress bar doesn't have interactive state updates
    }

    override fun draw(batch: SpriteBatch) {
        if (!visible) return

        // Switch to the solid white texture
        batch.switchTexture(whiteTexture)

        // 1. Draw background
        val bgColors = floatArrayOf(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        batch.draw(
            x = x + width / 2f,
            y = y + height / 2f,
            rotation = 0f,
            scaleX = width,
            scaleY = height,
            region = whiteRegion,
            color = bgColors
        )

        // 2. Draw foreground based on progress
        val coercedProgress = progress.coerceIn(0f, 1f)
        if (coercedProgress > 0f) {
            val fgWidth = width * coercedProgress
            val fgColors = floatArrayOf(foregroundColor.r, foregroundColor.g, foregroundColor.b, foregroundColor.a)
            batch.draw(
                x = x + fgWidth / 2f,
                y = y + height / 2f,
                rotation = 0f,
                scaleX = fgWidth,
                scaleY = height,
                region = whiteRegion,
                color = fgColors
            )
        }
    }
}
