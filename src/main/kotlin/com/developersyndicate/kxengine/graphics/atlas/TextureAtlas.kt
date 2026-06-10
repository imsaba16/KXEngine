package com.developersyndicate.kxengine.graphics.atlas

import com.developersyndicate.kxengine.graphics.Texture
import java.io.File

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

    companion object {
        fun load(atlasPath: String, texture: Texture): TextureAtlas {
            val localFile = File("src/main/resources/$atlasPath")
            val text = if (localFile.exists()) {
                localFile.readText()
            } else {
                val stream = TextureAtlas::class.java.classLoader.getResourceAsStream(atlasPath)
                    ?: error("Atlas file not found: $atlasPath")
                stream.bufferedReader().use { it.readText() }
            }

            val atlas = TextureAtlas(texture, texture.width.toFloat(), texture.height.toFloat())
            val lines = text.lines().map { it.trim() }

            var currentRegionName: String? = null
            var x = 0f
            var y = 0f
            var w = 0f
            var h = 0f

            for (line in lines) {
                if (line.isEmpty()) continue
                if (line.endsWith(".png") || line.endsWith(".jpg") || line.endsWith(".jpeg")) {
                    currentRegionName = null
                    continue
                }
                if (line.contains(":")) {
                    val key = line.substringBefore(":").trim()
                    val valStr = line.substringAfter(":").trim()
                    when (key) {
                        "xy" -> {
                            val parts = valStr.split(",").map { it.trim().toFloat() }
                            x = parts[0]
                            y = parts[1]
                        }
                        "size" -> {
                            val parts = valStr.split(",").map { it.trim().toFloat() }
                            w = parts[0]
                            h = parts[1]
                        }
                    }
                } else {
                    if (currentRegionName != null) {
                        atlas.define(currentRegionName, x, y, w, h)
                    }
                    currentRegionName = line
                }
            }
            if (currentRegionName != null) {
                atlas.define(currentRegionName, x, y, w, h)
            }

            return atlas
        }
    }
}
