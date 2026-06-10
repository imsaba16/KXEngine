package com.developersyndicate.kxengine.platform

import android.content.Context
import android.content.res.Configuration
import android.view.Surface
import com.developersyndicate.kxengine.AndroidWindow
import com.developersyndicate.kxengine.audio.AndroidAudioEngine
import com.developersyndicate.kxengine.audio.SoundEngine
import com.developersyndicate.kxengine.graphics.GlesGraphicsFactory
import com.developersyndicate.kxengine.input.AndroidInput
import com.developersyndicate.kxengine.input.Input

object AndroidPlatform {
    fun initialize(context: Context, surface: Surface, width: Int, height: Int) {
        KXPlatform.graphicsFactory = GlesGraphicsFactory()
        
        val androidAudio = AndroidAudioEngine(context)
        KXPlatform.audio = androidAudio
        SoundEngine.delegate = androidAudio
        
        val androidInput = AndroidInput()
        KXPlatform.input = androidInput
        Input.delegate = androidInput
        
        KXPlatform.fileSystem = AndroidFileSystem(context)
        
        KXPlatform.isTouchscreen = true
        
        // Detect physical keyboard connection.
        // On touch-only phones: keyboard=QWERTY (type), hardKeyboardHidden=YES (hidden/no slide-out).
        // On slide-out hardware keyboard: keyboard=QWERTY, hardKeyboardHidden=NO.
        // On emulator: keyboard=QWERTY, hardKeyboardHidden=NO (host PC keyboard is accessible).
        // We require BOTH conditions: a non-trivial keyboard type AND it's not hidden.
        val config = context.resources.configuration
        val hasKeyboard = config.keyboard != Configuration.KEYBOARD_NOKEYS &&
                          config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO
        KXPlatform.isKeyboardConnected = hasKeyboard
        
        KXPlatform.createWindow = { _, _, _ ->
            AndroidWindow(surface, width, height)
        }
    }
}
