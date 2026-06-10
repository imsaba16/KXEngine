package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.graphics.Transform
import com.developersyndicate.kxengine.graphics.Renderer
import com.developersyndicate.kxengine.graphics.Camera

open class Node(val name: String = "Node") {
    val transform = Transform()

    var parent: Node? = null
        private set

    private val children = mutableListOf<Node>()

    fun addChild(child: Node) {
        child.parent?.removeChild(child)
        child.parent = this
        child.transform.parent = this.transform
        children.add(child)
    }

    fun removeChild(child: Node) {
        if (children.remove(child)) {
            child.parent = null
            child.transform.parent = null
        }
    }

    fun getChildren(): List<Node> = children

    open fun update(dt: Float) {
        for (child in children) {
            child.update(dt)
        }
    }

    open fun render(renderer: Renderer, camera: Camera) {
        for (child in children) {
            child.render(renderer, camera)
        }
    }
}
