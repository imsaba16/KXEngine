package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch

class UIButton(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    var text: String,
    val font: BitmapFont,
    val textScale: Float = 1f,
    var textColor: Color = Color.WHITE,
    var backgroundColor: Color = Color(0.2f, 0.2f, 0.2f, 1f),
    var hoverColor: Color = Color(0.3f, 0.3f, 0.3f, 1f),
    var pressedColor: Color = Color(0.1f, 0.1f, 0.1f, 1f)
) : UIComponent(x, y, width, height) {

    var isHovered = false
        private set
    var isPressed = false
        private set

    var onClick: (() -> Unit)? = null
    
    private val whiteTexture = com.developersyndicate.kxengine.platform.KXPlatform.graphicsFactory.createTexture(1, 1, Color.WHITE)
    private val whiteRegion = AtlasRegion(0f, 0f, 1f, 1f)

    override fun update(
        dt: Float,
        mouseX: Float,
        mouseY: Float,
        isMouseDown: Boolean,
        wasMouseDown: Boolean
    ) {
        if (!active) return

        isHovered = mouseX >= x && mouseX <= x + width &&
                    mouseY >= y && mouseY <= y + height

        if (isHovered) {
            if (isMouseDown) {
                isPressed = true
            } else {
                if (isPressed) {
                    onClick?.invoke()
                    isPressed = false
                }
            }
        } else {
            isPressed = false
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (!visible) return

        // Select background color
        val currentBgColor = when {
            isPressed -> pressedColor
            isHovered -> hoverColor
            else -> backgroundColor
        }

        // Draw background
        batch.switchTexture(whiteTexture)
        val colorArr = floatArrayOf(currentBgColor.r, currentBgColor.g, currentBgColor.b, currentBgColor.a)
        batch.draw(
            x = x + width / 2f,
            y = y + height / 2f,
            rotation = 0f,
            scaleX = width,
            scaleY = height,
            region = whiteRegion,
            color = colorArr
        )

        // Draw text centered
        val textW = font.getTextWidth(text, textScale)
        val textH = font.lineHeight * textScale
        val textX = x + (width - textW) / 2f
        val textY = y + (height - textH) / 2f + textH

        // Draw the text
        batch.switchTexture(font.texture)
        font.drawText(batch, text, textX, textY, textScale, textColor)
    }
}
