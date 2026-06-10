package com.developersyndicate.kxengine.graphics

import org.lwjgl.opengl.GL30.*

class Framebuffer(val width: Int, val height: Int) {
    var id: Int = 0
        private set
    var textureId: Int = 0
        private set
    private var rboId: Int = 0

    init {
        id = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, id)

        textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0L)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0)

        rboId = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rboId)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboId)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            error("FBO creation failed!")
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id)
        // Use the FBO's own pixel dimensions (set at creation time using framebufferWidth/Height)
        glViewport(0, 0, width, height)
    }

    fun unbind(windowWidth: Int, windowHeight: Int) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        // windowWidth/Height must be physical pixel dimensions, not logical points
        glViewport(0, 0, windowWidth, windowHeight)
    }

    fun destroy() {
        glDeleteRenderbuffers(rboId)
        glDeleteTextures(textureId)
        glDeleteFramebuffers(id)
    }
}
