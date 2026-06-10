package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.EngineConfig
import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.input.Input

class UIManager {
    private val components = mutableListOf<UIComponent>()
    private var lastMouseDown = false

    private val defaultTexture = Texture(1, 1, Color.WHITE)

    fun add(component: UIComponent) {
        components.add(component)
    }

    fun remove(component: UIComponent) {
        components.remove(component)
    }

    fun clear() {
        components.clear()
    }

    fun update(dt: Float) {
        val mouseX = Input.mouseX
        // Flip Y coordinate to match OpenGL coordinates (bottom-left origin)
        val mouseY = EngineConfig.windowHeight - Input.mouseY
        val isMouseDown = Input.isMouseButtonDown(0)

        // Create a copy of the list to avoid ConcurrentModificationException if components are modified in callbacks
        val listToUpdate = components.toList()
        for (component in listToUpdate) {
            if (component.active) {
                component.update(dt, mouseX, mouseY, isMouseDown, lastMouseDown)
            }
        }

        lastMouseDown = isMouseDown
    }

    fun draw(batch: SpriteBatch) {
        val visibleComponents = components.filter { it.visible }
        if (visibleComponents.isEmpty()) return

        batch.begin(defaultTexture)
        for (component in visibleComponents) {
            component.draw(batch)
        }
        batch.end()
    }
}
