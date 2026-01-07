package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.debug.DebugGrid
import com.developersyndicate.kxengine.debug.DebugRect
import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.math.Mat4
import com.developersyndicate.kxengine.scene.Scene

class Renderer {

    private val debugGrid = DebugGrid()
    private var deadZoneRect: DebugRect? = null
    private val spriteBatch = SpriteBatch()

    private val shader = Shader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aUV;
            layout (location = 2) in vec4 aColor;

            uniform mat4 uVP;   // View * Projection

            out vec2 vUV;
            out vec4 vColor;

            void main() {
                vUV = aUV;
                vColor = aColor;
                gl_Position = uVP * vec4(aPos, 0.0, 1.0);
            }
        """.trimIndent(),

        fragmentSource = """
            #version 330 core
            in vec2 vUV;
            in vec4 vColor;

            uniform sampler2D uTexture;

            out vec4 FragColor;

            void main() {
                FragColor = texture(uTexture, vUV) * vColor;
            }
        """.trimIndent()
    )

    fun render(scene: Scene, camera: Camera, debug: Boolean) {
        shader.bind()

        shader.setMat4("uVP", camera.matrix().toFloatArray())

        if (debug) {
            shader.setVec4("uColor", Color(0.3f, 0.3f, 0.3f, 1f))
            debugGrid.draw()

            if (deadZoneRect == null) {
                deadZoneRect = DebugRect(
                    camera.deadZoneWidth,
                    camera.deadZoneHeight
                )
            }

            val dzModel = Mat4.translation(camera.position)
            shader.setMat4("uVP", camera.matrix().toFloatArray())
            shader.setVec4("uColor", Color(1f, 1f, 0f, 1f))
            deadZoneRect!!.draw()
        }

        for (obj in scene.all()) {
            shader.setMat4("uVP", camera.matrix().toFloatArray())
            obj.material.bind(shader)

            obj.mesh.bind()
            obj.mesh.draw()
            obj.mesh.unbind()
        }

        shader.unbind()
    }

    fun renderBatched(
        sprites: List<Renderable>,
        camera: Camera
    ) {
        if (sprites.isEmpty()) return

        require(sprites.all { it.material is TextureMaterial }) {
            "SpriteBatch supports only TextureMaterial"
        }

        shader.bind()

        shader.setMat4("uVP", camera.matrix().toFloatArray())

        val texture = (sprites.first().material as TextureMaterial).texture
        spriteBatch.begin(texture)

        for (obj in sprites) {
            val material = obj.material as TextureMaterial
            spriteBatch.draw(
                model = obj.transform.matrix(),
                region = material.region,
                color = floatArrayOf(1f, 1f, 1f, 1f)
            )
        }

        spriteBatch.end()
        shader.unbind()
    }

    fun destroy(mesh: TriangleMesh) {
        deadZoneRect?.destroy()
        debugGrid.destroy()
        spriteBatch.destroy()
        mesh.destroy()
        shader.destroy()
    }
}
