package com.developersyndicate.kxengine

interface Window {
    val framebufferWidth: Int
    val framebufferHeight: Int
    fun pollEvents()
    fun shouldClose() : Boolean
    fun destroy()
    fun swapBuffers()
}
