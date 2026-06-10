package com.developersyndicate.kxengine.platform

import com.developersyndicate.kxengine.Window
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.Framebuffer
import com.developersyndicate.kxengine.graphics.Mesh
import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.Renderer
import com.developersyndicate.kxengine.audio.IAudioEngine

interface IGraphicsFactory {
    fun createQuadMesh(): Mesh
    fun createTriangleMesh(vertices: FloatArray, indices: IntArray): Mesh
    fun createTexture(resourcePath: String): Texture
    fun createTexture(width: Int, height: Int, color: Color): Texture
    fun createShaderFromSource(vertexSource: String, fragmentSource: String): Shader
    fun createShaderFromFiles(vertexPath: String, fragmentPath: String): Shader
    fun createFramebuffer(width: Int, height: Int): Framebuffer
    fun createSpriteBatch(): SpriteBatch
    fun createRenderer(): Renderer
}

object KXPlatform {
    lateinit var graphicsFactory: IGraphicsFactory
    lateinit var audio: IAudioEngine
    lateinit var fileSystem: IFileSystem
    lateinit var input: com.developersyndicate.kxengine.input.IInput
    
    var isTouchscreen: Boolean = false
    var isKeyboardConnected: Boolean = true
    
    lateinit var createWindow: (width: Int, height: Int, title: String) -> Window
}
