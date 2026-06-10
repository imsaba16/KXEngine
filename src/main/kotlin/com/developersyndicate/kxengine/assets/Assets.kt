package com.developersyndicate.kxengine.assets

import com.developersyndicate.kxengine.graphics.shader.Shader

object Assets {

    private val textures = mutableMapOf<String, TextureAsset>()
    private val atlases = mutableMapOf<String, AtlasAsset>()
    private val shaders = mutableListOf<Shader>()

    private var lastPollTime = 0L

    fun texture(path: String): TextureAsset {
        return textures.getOrPut(path) {
            TextureAsset(path)
        }
    }

    fun atlas(
        path: String,
        width: Float,
        height: Float,
        columns: Int,
        rows: Int
    ): AtlasAsset {
        return atlases.getOrPut(path) {
            AtlasAsset(path, width, height, columns, rows)
        }
    }

    fun registerShader(shader: Shader) {
        shaders.add(shader)
    }

    fun updateHotReload() {
        val now = System.currentTimeMillis()
        if (now - lastPollTime > 1000) { // check every 1000ms
            lastPollTime = now
            textures.values.forEach { it.texture.checkAndReload() }
            shaders.forEach { it.checkAndReload() }
        }
    }

    fun disposeAll() {
        atlases.values.forEach { it.dispose() }
        textures.values.forEach { it.dispose() }
        shaders.forEach { it.destroy() }
        atlases.clear()
        textures.clear()
        shaders.clear()
    }
}