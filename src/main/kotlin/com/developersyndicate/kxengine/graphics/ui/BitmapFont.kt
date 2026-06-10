package com.developersyndicate.kxengine.graphics.ui

import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import java.io.File

class BitmapFont(val fntPath: String, val texture: Texture) {
    class Glyph(
        val id: Int,
        val region: AtlasRegion,
        val xOffset: Float,
        val yOffset: Float,
        val xAdvance: Float
    )

    private val glyphs = mutableMapOf<Int, Glyph>()
    var lineHeight = 16f
        private set

    init {
        loadFont()
    }

    private fun loadFont() {
        try {
            val f = File("src/main/resources/$fntPath")
            val text = if (f.exists()) {
                f.readText()
            } else {
                val stream = BitmapFont::class.java.classLoader.getResourceAsStream(fntPath)
                if (stream == null) {
                    println("Warning: Font file not found: $fntPath. Using fallback font mapping on atlas.")
                    createFallbackGlyphs()
                    return
                }
                stream.bufferedReader().use { it.readText() }
            }

            val lines = text.lines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("common")) {
                    lineHeight = getValue(trimmed, "lineHeight").toFloat()
                } else if (trimmed.startsWith("char ")) {
                    val id = getValue(trimmed, "id")
                    val x = getValue(trimmed, "x")
                    val y = getValue(trimmed, "y")
                    val w = getValue(trimmed, "width")
                    val h = getValue(trimmed, "height")
                    val xoff = getValue(trimmed, "xoffset")
                    val yoff = getValue(trimmed, "yoffset")
                    val xadv = getValue(trimmed, "xadvance")

                    val texW = texture.width.toFloat()
                    val texH = texture.height.toFloat()

                    val u0 = x.toFloat() / texW
                    val v0 = (texH - y.toFloat() - h.toFloat()) / texH
                    val u1 = (x.toFloat() + w.toFloat()) / texW
                    val v1 = (texH - y.toFloat()) / texH

                    val region = AtlasRegion(u0, v0, u1, v1)
                    glyphs[id] = Glyph(id, region, xoff.toFloat(), yoff.toFloat(), xadv.toFloat())
                }
            }
        } catch (e: Exception) {
            println("Warning: Error loading font file: ${e.message}. Using fallback font mapping on atlas.")
            createFallbackGlyphs()
        }
    }

    private fun getValue(line: String, key: String): Int {
        val regex = Regex("$key=(-?\\d+)")
        val match = regex.find(line)
        return match?.groupValues?.get(1)?.toInt() ?: 0
    }

    private fun createFallbackGlyphs() {
        val texW = texture.width.toFloat()
        val texH = texture.height.toFloat()

        val cellW = 16f
        val cellH = 16f
        val cols = (texW / cellW).toInt().coerceAtLeast(1)

        for (i in 32..126) {
            val index = i - 32
            val col = index % cols
            val row = index / cols
            val x = col * cellW
            val y = row * cellH

            val u0 = x / texW
            val v0 = (texH - y - cellH) / texH
            val u1 = (x + cellW) / texW
            val v1 = (texH - y) / texH

            val region = AtlasRegion(u0, v0, u1, v1)
            glyphs[i] = Glyph(i, region, 0f, 0f, 12f)
        }
        lineHeight = 16f
    }

    fun getTextWidth(text: String, scale: Float = 1f): Float {
        var width = 0f
        for (char in text) {
            val glyph = glyphs[char.code] ?: continue
            width += glyph.xAdvance * scale
        }
        return width
    }

    fun drawText(
        batch: SpriteBatch,
        text: String,
        x: Float,
        y: Float,
        scale: Float = 1f,
        color: Color = Color.WHITE
    ) {
        var cursorX = x
        val cArr = floatArrayOf(color.r, color.g, color.b, color.a)

        for (char in text) {
            val glyph = glyphs[char.code] ?: continue

            val w = (glyph.region.u1 - glyph.region.u0) * texture.width * scale
            val h = (glyph.region.v1 - glyph.region.v0) * texture.height * scale

            val px = cursorX + glyph.xOffset * scale
            val py = y - glyph.yOffset * scale

            batch.draw(
                x = px + w / 2f,
                y = py - h / 2f,
                rotation = 0f,
                scaleX = w,
                scaleY = h,
                region = glyph.region,
                color = cArr
            )

            cursorX += glyph.xAdvance * scale
        }
    }
}
