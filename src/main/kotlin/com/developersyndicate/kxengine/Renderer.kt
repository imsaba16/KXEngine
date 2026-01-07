package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.scene.Scene
import org.lwjgl.opengl.GL11.*
import com.developersyndicate.kxengine.debug.*
import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.math.*


class Renderer {
    private val debugGrid = DebugGrid()
    private var deadZoneRect: DebugRect? = null
    private val shader = Shader(
        vertexSource = """
        #version 330 core
        layout (location = 0) in vec2 aPos;
        layout (location = 1) in vec2 aUV;

        uniform mat4 uMVP;
        out vec2 vUV;

        void main() {
            vUV = aUV;
            gl_Position = uMVP * vec4(aPos, 0.0, 1.0);
        }
    """.trimIndent(),

        fragmentSource = """
        #version 330 core
        in vec2 vUV;
        uniform sampler2D uTexture;
        out vec4 FragColor;

        void main() {
            FragColor = texture(uTexture, vUV);
        }
    """.trimIndent()
    )



    fun render(scene: Scene, camera: Camera, debug: Boolean = true) {
        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        shader.bind()

        if (debug) {
            shader.setMat4("uMVP", camera.matrix().toFloatArray())
            shader.setVec4("uColor", Color(0.3f, 0.3f, 0.3f, 1f))
            debugGrid.draw()
            if (deadZoneRect == null) {
                deadZoneRect = DebugRect(
                    camera.deadZoneWidth,
                    camera.deadZoneHeight
                )
            }
            val dzTransform =
                Mat4.translation(camera.position)

            val dzMVP =
                camera.matrix() * dzTransform

            shader.setMat4("uMVP", dzMVP.toFloatArray())
            shader.setVec4("uColor", Color(1f, 1f, 0f, 1f))
            deadZoneRect!!.draw()
        }

        for (obj in scene.all()) {
            val mvp = camera.matrix() * obj.transform.matrix()
            shader.setMat4("uMVP", mvp.toFloatArray())

            obj.material.bind(shader)

            obj.mesh.bind()
            obj.mesh.draw()
            obj.mesh.unbind()
        }

        shader.unbind()
    }

    fun destroy(mesh: TriangleMesh) {
        deadZoneRect?.destroy()
        debugGrid.destroy()
        mesh.destroy()
        shader.destroy()
    }
}
