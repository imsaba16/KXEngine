package com.developersyndicate.kxengine.graphics

interface Framebuffer {
    val width: Int
    val height: Int
    val textureId: Int
    fun bind()
    fun unbind(windowWidth: Int, windowHeight: Int)
    fun destroy()
}
