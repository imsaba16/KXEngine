package com.developersyndicate.kxengine.scene

import com.developersyndicate.kxengine.audio.SoundSource
import com.developersyndicate.kxengine.audio.Sound

import com.developersyndicate.kxengine.platform.KXPlatform

class AudioNode(
    name: String = "AudioNode",
    val sound: Sound? = null,
    val loop: Boolean = false
) : Node(name) {
    val source = KXPlatform.audio.createSoundSource(loop)

    fun play(customSound: Sound? = null) {
        val s = customSound ?: sound ?: return
        source.play(s)
    }

    override fun update(dt: Float) {
        super.update(dt)
        val pos = transform.worldPosition
        source.setPosition(pos.x, pos.y, pos.z)
    }

    fun destroy() {
        source.destroy()
    }
}
