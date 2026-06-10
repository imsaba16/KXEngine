package com.developersyndicate.kxengine.graphics

import android.opengl.GLES30
import com.developersyndicate.kxengine.debug.GlesDebugGrid
import com.developersyndicate.kxengine.debug.GlesDebugLineRenderer
import com.developersyndicate.kxengine.debug.GlesDebugRect
import com.developersyndicate.kxengine.debug.Profiler
import com.developersyndicate.kxengine.graphics.batch.GlesSpriteBatch
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.graphics.shader.GlesShader
import com.developersyndicate.kxengine.graphics.ui.UIManager
import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.scene.Scene

class GlesRenderer : Renderer {
    override var ambientColor = Color(0.15f, 0.15f, 0.2f)
    override var pointLight: PointLight2D? = null

    private val debugGrid = GlesDebugGrid(100, 1.0f)
    private var deadZoneRect: GlesDebugRect? = null
    private val debugLines = GlesDebugLineRenderer()
    
    private val screenQuad = GlesFullscreenQuad()
    private val spriteBatch = GlesSpriteBatch()

    private val shader = GlesShader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aUV;
            layout (location = 2) in vec4 aColor;

            uniform mat4 uVP;   // View * Projection
            uniform vec4 uUVOffsetScale;

            out vec2 vUV;
            out vec4 vColor;
            out vec3 vWorldPos;

            void main() {
                vUV = aUV * uUVOffsetScale.zw + uUVOffsetScale.xy;
                vColor = aColor;
                vWorldPos = vec3(aPos, 0.0);
                gl_Position = uVP * vec4(aPos, 0.0, 1.0);
            }
        """.trimIndent(),

        fragmentSource = """
            #version 330 core
            in vec2 vUV;
            in vec4 vColor;
            in vec3 vWorldPos;

            uniform sampler2D uTexture;
            uniform sampler2D uNormalMap;
            uniform bool uUseTexture;
            uniform bool uUseNormalMap;
            uniform bool uUseLighting;

            uniform vec4 uColor;
            uniform vec3 uAmbientColor;
            uniform vec3 uLightPos;
            uniform vec3 uLightColor;
            uniform float uLightRadius;
            uniform float uLightIntensity;

            out vec4 FragColor;

            void main() {
                vec4 texColor = vec4(1.0);
                if (uUseTexture) {
                    texColor = texture(uTexture, vUV);
                }

                if (!uUseLighting) {
                    FragColor = texColor * vColor * uColor;
                    return;
                }

                vec3 normal;
                if (uUseNormalMap) {
                    vec3 normalMapVal = texture(uNormalMap, vUV).rgb;
                    normal = normalize(normalMapVal * 2.0 - 1.0);
                } else {
                    normal = vec3(0.0, 0.0, 1.0);
                }

                vec3 lightDir = uLightPos - vWorldPos;
                float dist = length(lightDir);
                vec3 L = normalize(lightDir);

                float diffuseFactor = max(dot(normal, L), 0.0);
                float attenuation = clamp(1.0 - dist / uLightRadius, 0.0, 1.0) * uLightIntensity;
                vec3 diffuseLight = diffuseFactor * uLightColor * attenuation;

                vec3 finalLight = uAmbientColor + diffuseLight;
                finalLight = clamp(finalLight, 0.0, 1.0);

                FragColor = vec4(texColor.rgb * vColor.rgb * uColor.rgb * finalLight, texColor.a * vColor.a * uColor.a);
            }
        """.trimIndent()
    )

    private val postProcessShader = GlesShader(
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
                    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
                    color = vec4(gray, gray, gray, color.a);
                }

                // Vignette
                vec2 uv = vUV * 2.0 - 1.0;
                float vignette = 1.0 - dot(uv, uv) * uVignetteStrength * 0.25;
                color.rgb *= clamp(vignette, 0.0, 1.0);

                FragColor = color;
            }
        """.trimIndent()
    )

    private val debugShader = GlesShader(
        vertexSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;

            uniform mat4 uVP;

            void main() {
                gl_Position = uVP * vec4(aPos, 0.0, 1.0);
            }
        """.trimIndent(),

        fragmentSource = """
            #version 330 core
            uniform vec4 uColor;
            out vec4 FragColor;

            void main() {
                FragColor = uColor;
            }
        """.trimIndent()
    )

    private fun applyLightingUniforms() {
        shader.setInt("uUseLighting", 1)
        shader.setVec3("uAmbientColor", ambientColor)
        val light = pointLight
        if (light != null) {
            shader.setVec3("uLightPos", light.position.x, light.position.y, 1.0f)
            shader.setVec3("uLightColor", light.color)
            shader.setFloat("uLightRadius", light.radius)
            shader.setFloat("uLightIntensity", light.intensity)
        } else {
            shader.setVec3("uLightPos", 0f, 0f, 1.0f)
            shader.setVec3("uLightColor", 0f, 0f, 0f)
            shader.setFloat("uLightRadius", 1.0f)
            shader.setFloat("uLightIntensity", 0.0f)
        }
    }

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        GLES30.glDisable(GLES30.GL_SCISSOR_TEST)
        GLES30.glClearColor(r, g, b, a)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    }

    override fun render(scene: Scene, camera: Camera, debug: Boolean) {
        shader.bind()

        shader.setMat4("uVP", camera.matrix().toFloatArray())
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
        shader.setInt("uUseTexture", 1)
        shader.setVec4("uColor", Color.WHITE)
        applyLightingUniforms()

        if (debug) {
            shader.setInt("uUseLighting", 0)
            shader.setInt("uUseTexture", 0)
            shader.setInt("uUseNormalMap", 0)

            shader.setVec4("uColor", Color(0.3f, 0.3f, 0.3f, 1f))
            debugGrid.draw()

            if (deadZoneRect == null) {
                deadZoneRect = GlesDebugRect(
                    camera.deadZoneWidth,
                    camera.deadZoneHeight
                )
            }

            shader.setMat4("uVP", camera.matrix().toFloatArray())
            shader.setVec4("uColor", Color(1f, 1f, 0f, 1f))
            deadZoneRect!!.draw()
        }

        for (obj in scene.all()) {
            shader.setMat4("uVP", camera.matrix().toFloatArray())
            applyLightingUniforms()

            val mat = obj.material
            if (mat is TextureMaterial) {
                mat.bind(shader)
                shader.setInt("uUseTexture", 1)
                val reg = mat.region
                shader.setVec4("uUVOffsetScale", Color(reg.u0, reg.v0, reg.u1 - reg.u0, reg.v1 - reg.v0))
            } else {
                mat.bind(shader)
                shader.setInt("uUseTexture", 0)
                shader.setInt("uUseNormalMap", 0)
                shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
            }

            obj.mesh.bind()
            obj.mesh.draw()
            obj.mesh.unbind()
            Profiler.drawCalls++
        }

        shader.unbind()
    }

    override fun renderSprites(sprites: List<Renderable>, camera: Camera) {
        if (sprites.isEmpty()) return

        shader.bind()
        shader.setMat4("uVP", camera.matrix().toFloatArray())
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
        shader.setInt("uUseTexture", 1)
        shader.setVec4("uColor", Color.WHITE)
        applyLightingUniforms()

        val sortedSprites = sprites.sortedBy { it.zIndex }
        val layers = sortedSprites.groupBy { it.zIndex }

        for ((_, layerSprites) in layers) {
            val batches = layerSprites.groupBy {
                val material = it.material as TextureMaterial
                material.texture to material.normalMap
            }
            for ((pair, batchSprites) in batches) {
                val (texture, normalMap) = pair
                
                if (normalMap != null) {
                    normalMap.bind(1)
                    shader.setInt("uNormalMap", 1)
                    shader.setInt("uUseNormalMap", 1)
                } else {
                    shader.setInt("uUseNormalMap", 0)
                }

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

    override fun renderParticles(particleSystem: ParticleSystem, camera: Camera) {
        shader.bind()
        shader.setMat4("uVP", camera.matrix().toFloatArray())
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
        shader.setInt("uUseTexture", 1)
        shader.setVec4("uColor", Color.WHITE)
        applyLightingUniforms()

        particleSystem.draw(spriteBatch)

        shader.unbind()
    }

    override fun renderUI(uiManager: UIManager, uiCamera: UICamera) {
        shader.bind()
        shader.setMat4("uVP", uiCamera.matrix.toFloatArray())
        shader.setVec4("uUVOffsetScale", Color(0f, 0f, 1f, 1f))
        shader.setInt("uUseTexture", 1)
        shader.setVec4("uColor", Color.WHITE)
        
        shader.setInt("uUseLighting", 0)
        shader.setInt("uUseNormalMap", 0)

        uiManager.draw(spriteBatch)

        shader.unbind()
    }

    override fun renderFramebuffer(
        fbo: Framebuffer,
        windowWidth: Int,
        windowHeight: Int,
        grayscale: Boolean,
        vignetteStrength: Float
    ) {
        // Ensure viewport is correctly set to full window dimensions
        GLES30.glViewport(0, 0, windowWidth, windowHeight)
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        postProcessShader.bind()
        
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fbo.textureId)
        postProcessShader.setInt("uTexture", 0)
        postProcessShader.setInt("uGrayscale", if (grayscale) 1 else 0)
        postProcessShader.setFloat("uVignetteStrength", vignetteStrength)

        screenQuad.bind()
        screenQuad.draw()
        screenQuad.unbind()

        postProcessShader.unbind()
        Profiler.drawCalls++
    }

    override fun renderColliders(colliders: List<Collider>, camera: Camera) {
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthMask(false)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glLineWidth(2.0f)

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
        GLES30.glDepthMask(true)
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    override fun destroy(mesh: Mesh) {
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
