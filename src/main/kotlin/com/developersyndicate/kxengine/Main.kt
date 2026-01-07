package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.input.Input
import com.developersyndicate.kxengine.math.Vec3
import com.developersyndicate.kxengine.scene.Scene
import com.developersyndicate.kxengine.graphics.material.ColorMaterial
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor


fun main() {
    val window = GlfwWindow(800, 600, "KXEngine")
    val time = Time()
    val loop = GameLoop(time)
    val renderer = Renderer()

    val mesh = TriangleMesh()
    val camera = Camera(2f, 2f)
    val quad = QuadMesh()
    val sprites = mutableListOf<Renderable>()

    val scene = Scene()
    camera.followSpeed = 8f
    camera.deadZoneWidth = 0.8f
    camera.deadZoneHeight = 0.5f

    val texture = Texture("assets/monster.png")

    val player = Renderable(
        mesh = quad,
        transform = Transform(),
        material = TextureMaterial(texture)
    )

    val other = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(0.6f, 0f, 0f)
        },
        material = ColorMaterial(Color.RED)
    )
    sprites.add(player)
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
                strength = 0.10f,
                duration = 0.35f
            )
        }
        camera.update(delta)
        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
//        renderer.render(scene = Scene(), camera = camera, debug = false)
        renderer.renderBatched(sprites, camera)
        renderer.render(scene, camera, debug = true)
        Input.endFrame()
        window.swapBuffers()
    }

    quad.destroy()
    texture.destroy()
    renderer.destroy(mesh)
    window.destroy()
}
