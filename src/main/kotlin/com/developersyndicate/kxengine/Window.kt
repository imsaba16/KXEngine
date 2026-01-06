package com.developersyndicate.kxengine

interface Window {
    fun pollEvents()
    fun shouldClose() : Boolean
    fun destroy()
    fun swapBuffers()
}