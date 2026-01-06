package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.input.Input
import com.developersyndicate.kxengine.math.Vec3
import com.developersyndicate.kxengine.scene.Scene
import org.lwjgl.glfw.GLFW.*

fun main() {
    val window = GlfwWindow(800, 600, "KXEngine")
    val time = Time()
    val loop = GameLoop(time)
    val renderer = Renderer()

    val mesh = TriangleMesh()
    val camera = Camera(2f, 2f)

    val scene = Scene()
    camera.followSpeed = 8f
    camera.deadZoneWidth = 0.8f
    camera.deadZoneHeight = 0.5f


    val player = Renderable(
        mesh = mesh,
        transform = Transform(),
        color = Color.GREEN
    )

    val other = Renderable(
        mesh = mesh,
        transform = Transform().apply {
            position = Vec3(0.6f, 0f, 0f)
        },
        color = Color.RED
    )


    scene.add(player)
    camera.target = player.transform
    camera.followSpeed = 4f
    scene.add(other)

    val speed = 1.0f

    loop.run { delta ->
        window.pollEvents()

        if (window.shouldClose()) {
            loop.stop()
        }

        if (Input.isKeyDown(GLFW_KEY_A)) {
            player.transform.position =
                player.transform.position.copy(x = player.transform.position.x - speed * delta)
        }
        if (Input.isKeyDown(GLFW_KEY_D)) {
            player.transform.position =
                player.transform.position.copy(x = player.transform.position.x + speed * delta)
        }
        if (Input.isKeyDown(GLFW_KEY_W)) {
            player.transform.position =
                player.transform.position.copy(y = player.transform.position.y + speed * delta)
        }
        if (Input.isKeyDown(GLFW_KEY_S)) {
            player.transform.position =
                player.transform.position.copy(y = player.transform.position.y - speed * delta)
        }
        if (Input.isKeyPressed(GLFW_KEY_SPACE)) {
            println("Shake Triggered")
            camera.shake(
                strength = 0.50f,
                duration = 0.35f
            )
        }
        camera.update(delta)
        renderer.render(scene, camera, true)
        Input.endFrame()
        window.swapBuffers()
    }

    renderer.destroy(mesh)
    window.destroy()
}
