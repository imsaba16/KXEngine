package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.graphics.Renderable

class Scene {

    private val objects = mutableListOf<Renderable>()

    fun add(renderable: Renderable) {
        objects += renderable
    }

    fun all(): List<Renderable> = objects
}