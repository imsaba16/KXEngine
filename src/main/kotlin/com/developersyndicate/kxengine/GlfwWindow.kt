package com.developersyndicate.kxengine

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import com.developersyndicate.kxengine.input.Input
import org.lwjgl.glfw.GLFWKeyCallbackI


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
        glfwSetKeyCallback(handle) { _, key, _, action, _ ->
            when (action) {
                GLFW_PRESS -> Input.setKey(key, true)
                GLFW_RELEASE -> Input.setKey(key, false)
            }
        }
        glfwSetCursorPosCallback(handle) { _, xpos, ypos ->
            Input.setMousePosition(xpos.toFloat(), ypos.toFloat())
        }
        glfwSetMouseButtonCallback(handle) { _, button, action, _ ->
            Input.setMouseButton(button, action == GLFW_PRESS)
        }
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
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