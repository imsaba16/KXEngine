package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.batch.GlesSpriteBatch
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.graphics.shader.GlesShader
import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.platform.IGraphicsFactory

class GlesGraphicsFactory : IGraphicsFactory {
    override fun createQuadMesh(): Mesh {
        return GlesQuadMesh()
    }

    override fun createTriangleMesh(vertices: FloatArray, indices: IntArray): Mesh {
        return GlesTriangleMesh()
    }

    override fun createTexture(resourcePath: String): Texture {
        return GlesTexture(resourcePath)
    }

    override fun createTexture(width: Int, height: Int, color: Color): Texture {
        return GlesTexture(width, height, color)
    }

    override fun createShaderFromSource(vertexSource: String, fragmentSource: String): Shader {
        return GlesShader(vertexSource, fragmentSource)
    }

    override fun createShaderFromFiles(vertexPath: String, fragmentPath: String): Shader {
        return GlesShader(vertexPath, fragmentPath, isPath = true)
    }

    override fun createFramebuffer(width: Int, height: Int): Framebuffer {
        return GlesFramebuffer(width, height)
    }

    override fun createSpriteBatch(): SpriteBatch {
        return GlesSpriteBatch()
    }

    override fun createRenderer(): Renderer {
        return GlesRenderer()
    }
}
