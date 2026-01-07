package com.developersyndicate.kxengine.assets

object Assets {

    private val textures = mutableMapOf<String, TextureAsset>()
    private val atlases = mutableMapOf<String, AtlasAsset>()

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

    fun disposeAll() {
        atlases.values.forEach { it.dispose() }
        textures.values.forEach { it.dispose() }
        atlases.clear()
        textures.clear()
    }
}