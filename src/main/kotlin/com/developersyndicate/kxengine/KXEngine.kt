package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.input.Input

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

    override fun start() {
        game.init(this)

        loop.run(
            updateFixed = { fixedDelta ->
                game.update(fixedDelta)
            },
            updateVariable = { delta ->
                window.pollEvents()
                if (window.shouldClose()) loop.stop()

                game.render(delta)

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
        window.destroy()
    }
}