package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.math.*
import org.lwjgl.opengl.GL11.*

class Renderer {

    private val shader = Shader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            uniform mat4 uMVP;

            void main() {
                gl_Position = uMVP * vec4(aPos, 0.0, 1.0);
            }
        """.trimIndent(),

        fragmentSource = """
            #version 330 core
            out vec4 FragColor;

            void main() {
                FragColor = vec4(0.2, 0.7, 0.3, 1.0);
            }
        """.trimIndent()
    )

    private val triangle = TriangleMesh()
    private val transform = Transform()
    private val camera = Camera(2f, 2f)
    var frameCount = 0

    fun render(time: Float) {
        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

//        if (frameCount++ % 120 == 0) {
//            println("DeltaTime: %.4f".format(time))
//        }

        transform.rotation += time

        val mvp = camera.matrix() * transform.matrix()

        shader.bind()
        shader.setMat4("uMVP", mvp.toFloatArray())
        triangle.bind()
        triangle.draw()
        triangle.unbind()
        shader.unbind()
    }

    fun destroy() {
        triangle.destroy()
        shader.destroy()
    }
}
