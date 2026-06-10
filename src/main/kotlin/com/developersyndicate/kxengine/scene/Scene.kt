package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.graphics.Renderable
import com.developersyndicate.kxengine.physics.Collider

class Scene {

    private val objects = mutableListOf<Renderable>()

    val root = Node("Root")

    fun add(renderable: Renderable) {
        objects += renderable
    }

    fun all(): List<Renderable> {
        val list = mutableListOf<Renderable>()
        list.addAll(objects)
        collectRenderables(root, list)
        return list
    }

    fun add(node: Node) {
        root.addChild(node)
    }

    fun remove(node: Node) {
        root.removeChild(node)
    }

    fun update(dt: Float) {
        root.update(dt)
    }

    fun getColliders(): List<Collider> {
        val list = mutableListOf<Collider>()
        collectColliders(root, list)
        return list
    }

    private fun collectRenderables(node: Node, outList: MutableList<Renderable>) {
        if (node is SpriteNode) {
            outList.add(node.renderable)
        }
        for (child in node.getChildren()) {
            collectRenderables(child, outList)
        }
    }

    private fun collectColliders(node: Node, outList: MutableList<Collider>) {
        if (node is CollisionNode) {
            outList.add(node.collider)
        }
        for (child in node.getChildren()) {
            collectColliders(child, outList)
        }
    }
}