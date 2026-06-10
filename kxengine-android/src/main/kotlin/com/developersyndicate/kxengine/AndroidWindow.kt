package com.developersyndicate.kxengine

import android.opengl.EGL14.*
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.util.Log
import android.view.Surface

class AndroidWindow(
    val surface: Surface,
    val initialWidth: Int,
    val initialHeight: Int
) : Window {

    private var eglDisplay: EGLDisplay = EGL_NO_DISPLAY
    private var eglContext: EGLContext = EGL_NO_CONTEXT
    private var eglSurface: EGLSurface = EGL_NO_SURFACE

    override var framebufferWidth: Int = initialWidth
    override var framebufferHeight: Int = initialHeight

    init {
        initEGL()
    }

    private fun initEGL() {
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL_NO_DISPLAY) {
            error("Unable to get EGL14 display")
        }

        val version = IntArray(2)
        if (!eglInitialize(eglDisplay, version, 0, version, 1)) {
            error("Unable to initialize EGL14")
        }

        val attribList = intArrayOf(
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_NONE
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0)) {
            error("Unable to choose config")
        }

        val config = configs[0] ?: error("No EGL config found")

        val contextAttribs = intArrayOf(
            EGL_CONTEXT_CLIENT_VERSION, 3,
            EGL_NONE
        )
        eglContext = eglCreateContext(eglDisplay, config, EGL_NO_CONTEXT, contextAttribs, 0)
        if (eglContext == EGL_NO_CONTEXT) {
            error("Failed to create EGL context")
        }

        val surfaceAttribs = intArrayOf(EGL_NONE)
        eglSurface = eglCreateWindowSurface(eglDisplay, config, surface, surfaceAttribs, 0)
        if (eglSurface == EGL_NO_SURFACE) {
            error("Failed to create EGL window surface")
        }

        if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            error("Failed to make EGL context current")
        }

        // Query the TRUE physical pixel dimensions from the EGL surface.
        // surfaceChanged() width/height can be logical (dp) on some drivers and
        // SurfaceView pixel sizes, but EGL reports the actual framebuffer size.
        val wBuf = IntArray(1)
        val hBuf = IntArray(1)
        eglQuerySurface(eglDisplay, eglSurface, EGL_WIDTH, wBuf, 0)
        eglQuerySurface(eglDisplay, eglSurface, EGL_HEIGHT, hBuf, 0)
        if (wBuf[0] > 0 && hBuf[0] > 0) {
            framebufferWidth = wBuf[0]
            framebufferHeight = hBuf[0]
        }
        Log.i("AndroidWindow", "initEGL complete. " +
            "requested=${initialWidth}x${initialHeight}  " +
            "eglSurface=${framebufferWidth}x${framebufferHeight}")
    }

    override fun pollEvents() {
        // Event polling on Android is handled asynchronously by Android touch/key listeners, so this is a no-op.
    }

    override fun shouldClose(): Boolean {
        return false // Lifecycle is controlled by the Android Activity
    }

    override fun swapBuffers() {
        eglSwapBuffers(eglDisplay, eglSurface)
    }

    override fun destroy() {
        if (eglDisplay != EGL_NO_DISPLAY) {
            eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT)
            if (eglSurface != EGL_NO_SURFACE) {
                eglDestroySurface(eglDisplay, eglSurface)
                eglSurface = EGL_NO_SURFACE
            }
            if (eglContext != EGL_NO_CONTEXT) {
                eglDestroyContext(eglDisplay, eglContext)
                eglContext = EGL_NO_CONTEXT
            }
            eglTerminate(eglDisplay)
            eglDisplay = EGL_NO_DISPLAY
        }
    }

    fun updateSize(width: Int, height: Int) {
        framebufferWidth = width
        framebufferHeight = height
    }
}
