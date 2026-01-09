package com.developersyndicate.kxengine.debug

import org.lwjgl.opengl.GL11.*

class DebugAABB {

    fun draw(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        glBegin(GL_LINE_LOOP)
        glVertex2f(minX, minY)
        glVertex2f(maxX, minY)
        glVertex2f(maxX, maxY)
        glVertex2f(minX, maxY)
        glEnd()
    }
}