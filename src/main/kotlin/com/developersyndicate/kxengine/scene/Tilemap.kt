package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.graphics.Renderable
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.Transform
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.graphics.atlas.TextureAtlas
import com.developersyndicate.kxengine.math.Vec3
import com.developersyndicate.kxengine.math.Vec2
import com.developersyndicate.kxengine.physics.Collider
import java.io.File

class TileLayer(val name: String, val width: Int, val height: Int, val data: IntArray)

class Tilemap(
    val width: Int,
    val height: Int,
    val tileWidth: Int,
    val tileHeight: Int,
    val layers: List<TileLayer>
) {
    companion object {
        fun load(path: String): Tilemap {
            val localFile = File("src/main/resources/$path")
            val text = if (localFile.exists()) {
                localFile.readText()
            } else {
                val stream = Tilemap::class.java.classLoader.getResourceAsStream(path)
                    ?: error("Tilemap file not found: $path")
                stream.bufferedReader().use { it.readText() }
            }

            val width = extractInt(text, "width")
            val height = extractInt(text, "height")
            val tileWidth = extractInt(text, "tilewidth")
            val tileHeight = extractInt(text, "tileheight")

            val layersList = mutableListOf<TileLayer>()
            val layerRegex = Regex("""\{[^{}]*?"name"\s*:\s*"([^"]+)"[^{}]*?"type"\s*:\s*"tilelayer"[^{}]*?"data"\s*:\s*\[([\d\s,]+)\][^{}]*?\}""")
            val matches = layerRegex.findAll(text)

            for (match in matches) {
                val name = match.groupValues[1]
                val dataStr = match.groupValues[2]
                val data = dataStr.split(",").map { it.trim().toInt() }.toIntArray()
                layersList.add(TileLayer(name, width, height, data))
            }

            return Tilemap(width, height, tileWidth, tileHeight, layersList)
        }

        private fun extractInt(json: String, key: String): Int {
            val regex = Regex(""""$key"\s*:\s*(\d+)""")
            val match = regex.find(json) ?: error("Failed to parse key: $key")
            return match.groupValues[1].toInt()
        }
    }

    fun generateRenderables(
        tilesetTexture: Texture,
        atlas: TextureAtlas,
        mesh: com.developersyndicate.kxengine.graphics.Mesh
    ): List<Renderable> {
        val renderables = mutableListOf<Renderable>()

        for (layer in layers) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val tileId = layer.data[y * width + x]
                    if (tileId > 0) {
                        val cols = (tilesetTexture.width / tileWidth)
                        val tileIndex = tileId - 1
                        val colIdx = tileIndex % cols
                        val rowIdx = tileIndex / cols

                        val regionName = "char_${rowIdx}_${colIdx}"
                        val region = try {
                            atlas.region(regionName)
                        } catch (e: Exception) {
                            atlas.region("char_0_0")
                        }

                        val material = TextureMaterial(tilesetTexture, region)
                        val t = Transform().apply {
                            position = Vec3(x.toFloat() * 0.5f - 5f, (height - y).toFloat() * 0.5f - 3f, 0f)
                            scale = Vec3(0.5f, 0.5f, 1f)
                        }

                        val col = Collider(t, Vec2(0.25f, 0.25f), collisionLayer = 4, collisionMask = -1)
                        renderables.add(Renderable(mesh, t, material, col, zIndex = 0))
                    }
                }
            }
        }
        return renderables
    }
}
