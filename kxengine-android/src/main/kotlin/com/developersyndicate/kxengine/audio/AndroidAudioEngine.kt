package com.developersyndicate.kxengine.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.developersyndicate.kxengine.platform.KXPlatform

class AndroidAudioEngine(private val context: Context) : IAudioEngine {
    private var soundPool: SoundPool? = null
    private var enabled = false

    override fun init() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()
        enabled = true
        println("SoundEngine: Android SoundPool initialized successfully.")
    }

    override fun isEnabled(): Boolean = enabled && soundPool != null

    override fun loadSound(path: String): Sound {
        val pool = soundPool ?: return AndroidSound(0)
        
        if (!KXPlatform.fileSystem.exists(path)) {
            println("Warning: Audio file not found: $path. Sound will be silent.")
            return AndroidSound(0)
        }

        return try {
            val assetPath = if (path.startsWith("assets/")) path.substring("assets/".length) else path
            val afd = context.assets.openFd(assetPath)
            val soundId = pool.load(afd, 1)
            AndroidSound(soundId)
        } catch (e: Exception) {
            println("Warning: Failed to load sound $path: ${e.message}. Sound will be silent.")
            AndroidSound(0)
        }
    }

    override fun createSoundSource(loop: Boolean): SoundSource {
        return AndroidSoundSource(soundPool, loop)
    }

    override fun destroy() {
        soundPool?.release()
        soundPool = null
        enabled = false
        println("SoundEngine: Android SoundPool destroyed.")
    }
}

class AndroidSound(val soundId: Int) : Sound {
    override fun destroy() {
        // Unloaded automatically when soundPool is released or can be manually managed if needed.
    }
}

class AndroidSoundSource(private val soundPool: SoundPool?, val loop: Boolean = false) : SoundSource {
    private var streamId: Int = 0
    private var volume: Float = 1.0f

    override fun play(sound: Sound) {
        val pool = soundPool ?: return
        val androidSound = sound as? AndroidSound ?: return
        if (androidSound.soundId == 0) return
        
        stop()
        
        val loopValue = if (loop) -1 else 0
        streamId = pool.play(androidSound.soundId, volume, volume, 1, loopValue, 1.0f)
    }

    override fun stop() {
        val pool = soundPool ?: return
        if (streamId != 0) {
            pool.stop(streamId)
            streamId = 0
        }
    }

    override fun pause() {
        val pool = soundPool ?: return
        if (streamId != 0) {
            pool.pause(streamId)
        }
    }

    override fun setPosition(x: Float, y: Float, z: Float) {
        // 2D stereo panning could be computed based on camera position, left as a no-op for now.
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        val pool = soundPool ?: return
        if (streamId != 0) {
            pool.setVolume(streamId, this.volume, this.volume)
        }
    }

    override fun isPlaying(): Boolean {
        return streamId != 0
    }

    override fun destroy() {
        stop()
    }
}
