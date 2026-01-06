package com.developersyndicate.kxengine

fun main() {
    val window = GlfwWindow(800, 600, "KXEngine")
    val time = Time()
    val loop = GameLoop(time)
    val renderer = Renderer()

    loop.run { delta ->
        window.pollEvents()
        if (window.shouldClose()) {
            loop.stop()
        }

        renderer.render(delta)
        window.swapBuffers()
    }
    renderer.destroy()
    window.destroy()
}
