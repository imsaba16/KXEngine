package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.graphics.batch.SpriteBatch

abstract class UIComponent(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {
    var visible = true
    var active = true

    abstract fun update(
        dt: Float,
        mouseX: Float,
        mouseY: Float,
        isMouseDown: Boolean,
        wasMouseDown: Boolean
    )
    
    abstract fun draw(batch: SpriteBatch)
}
