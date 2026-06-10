package com.developersyndicate.kxengine.graphics

import android.opengl.GLES30

class GlesFramebuffer(override val width: Int, override val height: Int) : Framebuffer {
    var id: Int = 0
        private set
    override var textureId: Int = 0
        private set
    private var rboId: Int = 0

    init {
        val temp = IntArray(1)
        
        GLES30.glGenFramebuffers(1, temp, 0)
        id = temp[0]
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, id)

        GLES30.glGenTextures(1, temp, 0)
        textureId = temp[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
            GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D, textureId, 0
        )

        GLES30.glGenRenderbuffers(1, temp, 0)
        rboId = temp[0]
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, rboId)
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, width, height)
        GLES30.glFramebufferRenderbuffer(
            GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT,
            GLES30.GL_RENDERBUFFER, rboId
        )

        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            error("FBO creation failed!")
        }

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }

    override fun bind() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, id)
        GLES30.glViewport(0, 0, width, height)
    }

    override fun unbind(windowWidth: Int, windowHeight: Int) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glViewport(0, 0, windowWidth, windowHeight)
    }

    override fun destroy() {
        GLES30.glDeleteRenderbuffers(1, intArrayOf(rboId), 0)
        GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
        GLES30.glDeleteFramebuffers(1, intArrayOf(id), 0)
    }
}
