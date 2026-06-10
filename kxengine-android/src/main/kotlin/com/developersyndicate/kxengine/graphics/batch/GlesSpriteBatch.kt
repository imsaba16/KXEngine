package com.developersyndicate.kxengine.graphics.batch

import android.opengl.GLES30
import com.developersyndicate.kxengine.graphics.Texture
import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.math.Mat4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GlesSpriteBatch(
    private val maxSprites: Int = 1000
) : SpriteBatch {
    private val floatsPerVertex = 8
    private val verticesPerSprite = 6
    private val bufferSize = maxSprites * verticesPerSprite * floatsPerVertex

    private val vaoId: Int
    private val vboId: Int
    private val byteBuffer = ByteBuffer.allocateDirect(bufferSize * 4).order(ByteOrder.nativeOrder())
    private val buffer: FloatBuffer = byteBuffer.asFloatBuffer()

    private var spriteCount = 0
    private var currentTexture: Texture? = null

    init {
        val temp = IntArray(1)
        GLES30.glGenVertexArrays(1, temp, 0)
        vaoId = temp[0]

        GLES30.glGenBuffers(1, temp, 0)
        vboId = temp[0]

        GLES30.glBindVertexArray(vaoId)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            bufferSize * 4,
            null, // Allocate storage
            GLES30.GL_DYNAMIC_DRAW
        )

        val stride = floatsPerVertex * 4

        // position (location = 0)
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, stride, 0)
        GLES30.glEnableVertexAttribArray(0)

        // uv (location = 1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, stride, 2 * 4)
        GLES30.glEnableVertexAttribArray(1)

        // color (location = 2)
        GLES30.glVertexAttribPointer(2, 4, GLES30.GL_FLOAT, false, stride, 4 * 4)
        GLES30.glEnableVertexAttribArray(2)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    override fun begin(texture: Texture) {
        spriteCount = 0
        buffer.clear()
        currentTexture = texture
    }

    override fun switchTexture(texture: Texture) {
        if (currentTexture != texture) {
            if (spriteCount > 0) {
                end()
            }
            begin(texture)
        }
    }

    override fun draw(
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

    override fun draw(
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

    override fun setBlendMode(blendMode: BlendMode) {
        if (blendMode == BlendMode.ADDITIVE) {
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE)
        } else {
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    override fun end() {
        buffer.flip()

        currentTexture?.bind(0)

        GLES30.glBindVertexArray(vaoId)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        // Reset byteBuffer position and limit according to float buffer
        byteBuffer.position(0)
        byteBuffer.limit(buffer.limit() * 4)

        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, byteBuffer.limit(), byteBuffer)

        GLES30.glDrawArrays(
            GLES30.GL_TRIANGLES,
            0,
            spriteCount * verticesPerSprite
        )

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    override fun destroy() {
        GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }
}
