package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.input.Input
import com.developersyndicate.kxengine.audio.SoundEngine
import com.developersyndicate.kxengine.assets.Assets
import com.developersyndicate.kxengine.debug.Profiler

class KXEngine(
    val game: Game,
    val width: Int = 800,
    val height: Int = 600,
    val title: String = "KXEngine"
) : Engine {
    val window = GlfwWindow(width, height, title)
    val time = Time()
    val loop = GameLoop(time)
    val renderer = Renderer()

    /** Physical framebuffer size in pixels (differs from width/height on Retina displays). */
    val framebufferWidth: Int  get() = window.framebufferWidth
    val framebufferHeight: Int get() = window.framebufferHeight


    override fun start() {
        SoundEngine.init()

        game.init(this)

        loop.run(
            updateFixed = { fixedDelta ->
                game.update(fixedDelta)
            },
            updateVariable = { delta ->
                window.pollEvents()
                if (window.shouldClose()) loop.stop()

                if (EngineConfig.hotReloadEnabled) {
                    Assets.updateHotReload()
                }

                game.render(delta)

                Profiler.tickFrame()
                Input.endFrame()
                window.swapBuffers()
            }
        )

        game.dispose()
        cleanup()
    }

    override fun stop() {
        loop.stop()
    }

    private fun cleanup() {
        SoundEngine.destroy()
        window.destroy()
    }
}