package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.assets.Assets
import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.graphics.animation.Animator
import com.developersyndicate.kxengine.graphics.animation.SpriteAnimation
import com.developersyndicate.kxengine.input.Input
import com.developersyndicate.kxengine.math.*
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.physics.Physics
import com.developersyndicate.kxengine.scene.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

fun main() {
    val window = GlfwWindow(800, 600, "KXEngine")
    val time = Time()
    val loop = GameLoop(time)
    val renderer = Renderer()

    val camera = Camera(2f, 2f)
    val quad = QuadMesh()
    val sprites = mutableListOf<Renderable>()
    val animator = Animator()
    val physics = Physics()
    var showColliders = true

    camera.followSpeed = 4f
    camera.deadZoneWidth = 0.8f
    camera.deadZoneHeight = 0.5f

    // === ASSETS ===
    val atlasAsset = Assets.atlas(
        path = "assets/atlas.png",
        width = 192f,
        height = 128f,
        columns = 12,
        rows = 8
    )

    val atlas = atlasAsset.atlas
    val atlasTexture = atlasAsset.textureAsset.texture

    // === MATERIALS ===
    val playerMaterial = TextureMaterial(
        texture = atlasTexture,
        region = atlas.region("char_2_5")
    )

    val enemyMaterial = TextureMaterial(
        texture = atlasTexture,
        region = atlas.region("char_5_6")
    )

    // === ANIMATIONS ===
    val idleAnimation = SpriteAnimation(
        frames = listOf(atlas.region("char_2_0")),
        frameDuration = 1f,
        loop = true
    )

    val walkAnimation = SpriteAnimation(
        frames = listOf(
            atlas.region("char_2_0"),
            atlas.region("char_2_1"),
            atlas.region("char_2_2")
        ),
        frameDuration = 0.15f,
        loop = true
    )

    animator.play(idleAnimation)

    // === ENTITIES ===
    val player = Renderable(
        mesh = quad,
        transform = Transform(),
        material = playerMaterial
    )

    val enemy = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(1f, 0f, 0f)
        },
        material = enemyMaterial
    )

    sprites.add(player)
    sprites.add(enemy)

    camera.target = player.transform

    // === COLLIDERS ===
    val playerCollider = Collider(
        transform = player.transform,
        halfSize = Vec2(0.5f, 0.5f)
    )

    val wallCollider = Collider(
        transform = enemy.transform,
        halfSize = Vec2(0.5f, 0.5f)
    )
    val debugColliders = listOf(
        playerCollider,
        wallCollider
    )

    val speed = 1.0f


    // === GAME LOOP ===
    loop.run { delta ->
        window.pollEvents()
        if (window.shouldClose()) loop.stop()

        // 🔥 SAVE OLD POSITION (PER FRAME)
        val oldPos = player.transform.position

        // === INPUT MOVEMENT ===
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

        // === COLLISION RESOLUTION ===
        if (physics.resolve(playerCollider, listOf(wallCollider))) {
            player.transform.position = oldPos
        }

        if (Input.isKeyPressed(GLFW_KEY_F1)) {
            showColliders = !showColliders
        }

        // === ANIMATION STATE ===
        val moving =
            Input.isKeyDown(GLFW_KEY_A) ||
                    Input.isKeyDown(GLFW_KEY_D) ||
                    Input.isKeyDown(GLFW_KEY_W) ||
                    Input.isKeyDown(GLFW_KEY_S)

        animator.play(if (moving) walkAnimation else idleAnimation)
        animator.update(delta)
        playerMaterial.region = animator.currentFrame()

        // === CAMERA ===
        if (Input.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.shake(0.10f, 0.35f)
        }
        camera.update(delta)

        // === RENDER ===
        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderer.renderSprites(sprites, camera)
        renderer.render(Scene(), camera, debug = true)
        if (showColliders) {
            renderer.renderColliders(debugColliders, camera)
        }

        Input.endFrame()
        window.swapBuffers()
    }

    // === CLEANUP ===
    quad.destroy()
    Assets.disposeAll()
    renderer.destroy(TriangleMesh())
    window.destroy()
}