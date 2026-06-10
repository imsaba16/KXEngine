package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.batch.LwjglSpriteBatch
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.graphics.shader.LwjglShader
import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.platform.IGraphicsFactory

class LwjglGraphicsFactory : IGraphicsFactory {
    override fun createQuadMesh(): Mesh {
        return LwjglQuadMesh()
    }

    override fun createTriangleMesh(vertices: FloatArray, indices: IntArray): Mesh {
        return LwjglTriangleMesh()
    }

    override fun createTexture(resourcePath: String): Texture {
        return LwjglTexture(resourcePath)
    }

    override fun createTexture(width: Int, height: Int, color: Color): Texture {
        return LwjglTexture(width, height, color)
    }

    override fun createShaderFromSource(vertexSource: String, fragmentSource: String): Shader {
        return LwjglShader(vertexSource, fragmentSource)
    }

    override fun createShaderFromFiles(vertexPath: String, fragmentPath: String): Shader {
        return LwjglShader(vertexPath, fragmentPath, isPath = true)
    }

    override fun createFramebuffer(width: Int, height: Int): Framebuffer {
        return LwjglFramebuffer(width, height)
    }

    override fun createSpriteBatch(): SpriteBatch {
        return LwjglSpriteBatch()
    }

    override fun createRenderer(): Renderer {
        return LwjglRenderer()
    }
}
