package com.developersyndicate.kxengine.graphics.batch

import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.math.Mat4
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class SpriteBatch(
    private val maxSprites: Int = 1000
) {
    private val floatsPerVertex = 8
    private val verticesPerSprite = 6
    private val bufferSize =
        maxSprites * verticesPerSprite * floatsPerVertex

    private val vaoId: Int
    private val vboId: Int
    private val buffer = MemoryUtil.memAllocFloat(bufferSize)

    private var spriteCount = 0
    private var currentTexture: Texture? = null

    init {
        vaoId = glGenVertexArrays()
        vboId = glGenBuffers()

        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        glBufferData(
            GL_ARRAY_BUFFER,
            bufferSize.toLong() * Float.SIZE_BYTES,
            GL_DYNAMIC_DRAW
        )

        val stride = floatsPerVertex * Float.SIZE_BYTES

        // position
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0)
        glEnableVertexAttribArray(0)

        // uv
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(1)

        // color
        glVertexAttribPointer(2, 4, GL_FLOAT, false, stride, 4L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(2)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun begin(texture: Texture) {
        spriteCount = 0
        buffer.clear()
        currentTexture = texture
    }

    fun switchTexture(texture: Texture) {
        if (currentTexture != texture) {
            if (spriteCount > 0) {
                end()
            }
            begin(texture)
        }
    }

    fun draw(
        model: Mat4,
        region: AtlasRegion,
        color: FloatArray
    ) {
        val positions = arrayOf(
            -0.5f to  0.5f,
            -0.5f to -0.5f,
            0.5f to -0.5f,
            -0.5f to  0.5f,
            0.5f to -0.5f,
            0.5f to  0.5f
        )

        val uvs = arrayOf(
            region.u0 to region.v1,
            region.u0 to region.v0,
            region.u1 to region.v0,
            region.u0 to region.v1,
            region.u1 to region.v0,
            region.u1 to region.v1
        )

        val m = model.toFloatArray()

        for (i in 0 until 6) {
            val (x, y) = positions[i]
            val (u, v) = uvs[i]

            val px = m[0] * x + m[4] * y + m[12]
            val py = m[1] * x + m[5] * y + m[13]

            buffer.put(px)
            buffer.put(py)
            buffer.put(u)
            buffer.put(v)
            buffer.put(color[0])
            buffer.put(color[1])
            buffer.put(color[2])
            buffer.put(color[3])
        }

        spriteCount++
    }

    fun draw(
        x: Float,
        y: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float,
        region: AtlasRegion,
        color: FloatArray
    ) {
        val positions = arrayOf(
            -0.5f to  0.5f,
            -0.5f to -0.5f,
            0.5f to -0.5f,
            -0.5f to  0.5f,
            0.5f to -0.5f,
            0.5f to  0.5f
        )

        val uvs = arrayOf(
            region.u0 to region.v1,
            region.u0 to region.v0,
            region.u1 to region.v0,
            region.u0 to region.v1,
            region.u1 to region.v0,
            region.u1 to region.v1
        )

        val cos = if (rotation != 0f) Math.cos(rotation.toDouble()).toFloat() else 1f
        val sin = if (rotation != 0f) Math.sin(rotation.toDouble()).toFloat() else 0f

        for (i in 0 until 6) {
            val (vx, vy) = positions[i]
            val (u, v) = uvs[i]

            val sx = vx * scaleX
            val sy = vy * scaleY

            val px = sx * cos - sy * sin + x
            val py = sx * sin + sy * cos + y

            buffer.put(px)
            buffer.put(py)
            buffer.put(u)
            buffer.put(v)
            buffer.put(color[0])
            buffer.put(color[1])
            buffer.put(color[2])
            buffer.put(color[3])
        }

        spriteCount++
    }


    fun end() {
        buffer.flip()

        currentTexture?.bind(0)

        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)

        glDrawArrays(
            GL_TRIANGLES,
            0,
            spriteCount * verticesPerSprite
        )

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun destroy() {
        MemoryUtil.memFree(buffer)
        glDeleteBuffers(vboId)
        glDeleteVertexArrays(vaoId)
    }
}
