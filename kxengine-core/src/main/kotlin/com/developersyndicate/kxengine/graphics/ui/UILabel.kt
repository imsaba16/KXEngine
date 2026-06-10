package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch

class UILabel(
    x: Float,
    y: Float,
    text: String,
    val font: BitmapFont,
    var scale: Float = 1f,
    var color: Color = Color.WHITE
) : UIComponent(x, y, 0f, 0f) {

    var text = text
        set(value) {
            field = value
            updateBounds()
        }

    init {
        updateBounds()
    }

    private fun updateBounds() {
        width = font.getTextWidth(text, scale)
        height = font.lineHeight * scale
    }

    override fun update(
        dt: Float,
        mouseX: Float,
        mouseY: Float,
        isMouseDown: Boolean,
        wasMouseDown: Boolean
    ) {
        // Static component, no interaction update needed
    }

    override fun draw(batch: SpriteBatch) {
        if (!visible) return
        batch.switchTexture(font.texture)
        // Since y is bottom-left and drawText expects baseline/top, we offset by height
        font.drawText(batch, text, x, y + height, scale, color)
    }
}
