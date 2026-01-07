package com.developersyndicate.kxengine.graphics.atlas

import com.developersyndicate.kxengine.graphics.Texture

class TextureAtlas(
    val texture: Texture,
    private val atlasWidth: Float,
    private val atlasHeight: Float
) {
    private val regions = mutableMapOf<String, AtlasRegion>()

    fun define(
        name: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val u0 = x / atlasWidth
        val v0 = y / atlasHeight
        val u1 = (x + width) / atlasWidth
        val v1 = (y + height) / atlasHeight

        regions[name] = AtlasRegion(u0, v0, u1, v1)
    }

    fun region(name: String): AtlasRegion =
        regions[name] ?: error("Atlas region '$name' not found")

    fun defineGrid(
        prefix: String,
        columns: Int,
        rows: Int
    ) {
        val cellWidth = atlasWidth / columns
        val cellHeight = atlasHeight / rows

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val name = "${prefix}_${row}_${col}"

                define(
                    name = name,
                    x = col * cellWidth,
                    y = row * cellHeight,
                    width = cellWidth,
                    height = cellHeight
                )
            }
        }
    }

}
