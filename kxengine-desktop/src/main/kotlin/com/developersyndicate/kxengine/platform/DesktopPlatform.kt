package com.developersyndicate.kxengine.platform

import com.developersyndicate.kxengine.GlfwWindow
import com.developersyndicate.kxengine.audio.LwjglAudioEngine
import com.developersyndicate.kxengine.audio.SoundEngine
import com.developersyndicate.kxengine.graphics.LwjglGraphicsFactory
import com.developersyndicate.kxengine.input.GlfwInput
import com.developersyndicate.kxengine.input.Input

object DesktopPlatform {
    fun initialize() {
        KXPlatform.graphicsFactory = LwjglGraphicsFactory()
        
        val lwjglAudio = LwjglAudioEngine()
        KXPlatform.audio = lwjglAudio
        SoundEngine.delegate = lwjglAudio
        
        val glfwInput = GlfwInput()
        KXPlatform.input = glfwInput
        Input.delegate = glfwInput
        
        KXPlatform.fileSystem = DesktopFileSystem()
        
        KXPlatform.isTouchscreen = false
        KXPlatform.isKeyboardConnected = true
        
        KXPlatform.createWindow = { width, height, title ->
            GlfwWindow(width, height, title)
        }
    }
}
