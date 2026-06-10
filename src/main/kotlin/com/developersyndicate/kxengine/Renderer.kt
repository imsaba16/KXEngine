package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.debug.*
import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.math.Mat4
import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.scene.Scene
import com.developersyndicate.kxengine.graphics.shader.DebugShader
import com.developersyndicate.kxengine.debug.DebugLineRenderer
import org.lwjgl.opengl.GL11.*

class Renderer {

    private val debugGrid = DebugGrid()
    private var deadZoneRect: DebugRect? = null
    private val spriteBatch = SpriteBatch()
    private val debugShader = DebugShader()
    private val debugLines = DebugLineRenderer()

    private val shader = Shader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aUV;
            layout (location = 2) in vec4 aColor;

            uniform mat4 uVP;   // View * Projection
            uniform vec4 uUVOffsetScale;

            out vec2 vUV;
            out vec4 vColor;

            void main() {
                vUV = aUV * uUVOffsetScale.zw + uUVOffsetScale.xy;
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
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))

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

            if (obj.material is TextureMaterial) {
                val reg = obj.material.region
                shader.setVec4("uUVOffsetScale", Color(reg.u0, reg.v0, reg.u1 - reg.u0, reg.v1 - reg.v0))
            } else {
                shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
            }

            obj.mesh.bind()
            obj.mesh.draw()
            obj.mesh.unbind()
        }

        shader.unbind()
    }

    fun renderSprites(
        sprites: List<Renderable>,
        camera: Camera
    ) {
        if (sprites.isEmpty()) return

        shader.bind()
        shader.setMat4("uVP", camera.matrix().toFloatArray())
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))

        val batches = sprites.groupBy {
            val material = it.material as TextureMaterial
            material.texture
        }

        for ((texture, batchSprites) in batches) {
            spriteBatch.begin(texture)
            for (obj in batchSprites) {
                val material = obj.material as TextureMaterial
                spriteBatch.draw(
                    model = obj.transform.matrix(),
                    region = material.region,
                    color = floatArrayOf(1f, 1f, 1f, 1f)
                )
            }
            spriteBatch.end()
        }

        shader.unbind()
    }

    fun renderColliders(
        colliders: List<Collider>,
        camera: Camera
    ) {
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glDisable(GL_CULL_FACE)
        glLineWidth(2.0f)

        debugShader.bind()
        debugShader.setMat4("uVP", camera.matrix().toFloatArray())
        debugShader.setVec4("uColor", Color(1f, 0f, 0f, 0.4f))

        for (collider in colliders) {
            val box = collider.aabb()
            debugLines.drawRect(
                minX = box.min.x,
                minY = box.min.y,
                maxX = box.max.x,
                maxY = box.max.y
            )
        }

        debugShader.unbind()
        glDepthMask(true)
        glEnable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
    }

    fun destroy(mesh: TriangleMesh) {
        deadZoneRect?.destroy()
        debugGrid.destroy()
        debugLines.destroy()
        spriteBatch.destroy()
        debugLines.destroy()
        debugShader.destroy()
        mesh.destroy()
        shader.destroy()
    }
}
