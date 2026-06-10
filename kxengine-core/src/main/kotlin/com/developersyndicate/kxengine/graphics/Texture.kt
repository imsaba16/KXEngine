package com.developersyndicate.kxengine.graphics

interface Texture {
    val id: Int
    val width: Int
    val height: Int
    fun bind(unit: Int = 0)
    fun destroy()
    fun checkAndReload(): Boolean = false
}
