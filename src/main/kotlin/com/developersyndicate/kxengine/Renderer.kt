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
import org.lwjgl.opengl.GL13.*

class Renderer {

    private val debugGrid = DebugGrid()
    private var deadZoneRect: DebugRect? = null
    private val spriteBatch = SpriteBatch()
    private val debugShader = DebugShader()
    private val debugLines = DebugLineRenderer()
    
    private val screenQuad = QuadMesh()

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

    private val postProcessShader = Shader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aUV;

            out vec2 vUV;

            void main() {
                vUV = aUV;
                gl_Position = vec4(aPos, 0.0, 1.0);
            }
        """.trimIndent(),

        fragmentSource = """
            #version 330 core
            in vec2 vUV;
            uniform sampler2D uTexture;
            uniform float uVignetteStrength;
            uniform bool uGrayscale;

            out vec4 FragColor;

            void main() {
                vec4 color = texture(uTexture, vUV);
                if (uGrayscale) {
                    float avg = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
                    color = vec4(avg, avg, avg, color.a);
                }
                vec2 uv = vUV - 0.5;
                float dist = length(uv);
                float vignette = smoothstep(0.8, 0.4, dist * uVignetteStrength);
                FragColor = vec4(color.rgb * vignette, color.a);
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
            Profiler.drawCalls++
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

        // Sort renderables by z-index layer
        val sortedSprites = sprites.sortedBy { it.zIndex }
        val layers = sortedSprites.groupBy { it.zIndex }

        for ((_, layerSprites) in layers) {
            val batches = layerSprites.groupBy {
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
                Profiler.drawCalls++
            }
        }

        shader.unbind()
    }

    fun renderFramebuffer(
        fbo: Framebuffer,
        windowWidth: Int,
        windowHeight: Int,
        grayscale: Boolean = false,
        vignetteStrength: Float = 0f
    ) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        postProcessShader.bind()
        
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, fbo.textureId)
        postProcessShader.setInt("uTexture", 0)
        postProcessShader.setInt("uGrayscale", if (grayscale) 1 else 0)
        postProcessShader.setFloat("uVignetteStrength", vignetteStrength)

        screenQuad.bind()
        screenQuad.draw()
        screenQuad.unbind()

        postProcessShader.unbind()
        Profiler.drawCalls++
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
        debugShader.destroy()
        screenQuad.destroy()
        postProcessShader.destroy()
        mesh.destroy()
        shader.destroy()
    }
}
