package com.developersyndicate.kxengine

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

class GlfwWindow(width: Int, height: Int, title: String) : Window {
    val handle: Long

    init {
        if (!glfwInit()) error("GLFW init failed")
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)

        handle = glfwCreateWindow(width, height, title, 0, 0)
        if (handle == 0L) error("Window creation failed")
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
        glfwSwapInterval(1)
    }
    override fun pollEvents() {
        glfwPollEvents()
    }

    override fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    override fun destroy() {

        glfwDestroyWindow(handle)
        glfwTerminate()
    }

    override fun swapBuffers() {
        glfwSwapBuffers(handle)
    }

}