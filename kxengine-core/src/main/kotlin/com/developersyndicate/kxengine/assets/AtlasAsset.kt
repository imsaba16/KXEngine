package com.developersyndicate.kxengine.assets

import com.developersyndicate.kxengine.graphics.atlas.TextureAtlas

class AtlasAsset(
    path: String,
    width: Float,
    height: Float,
    columns: Int,
    rows: Int
) {
    val textureAsset = TextureAsset(path)

    val atlas = TextureAtlas(
        texture = textureAsset.texture,
        atlasWidth = width,
        atlasHeight = height
    )

    init {
        atlas.defineGrid(
            prefix = "char",
            columns = columns,
            rows = rows
        )
    }

    fun dispose() {
        textureAsset.dispose()
    }
}