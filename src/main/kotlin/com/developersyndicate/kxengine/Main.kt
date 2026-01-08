package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.assets.Assets
import com.developersyndicate.kxengine.combat.Damage
import com.developersyndicate.kxengine.combat.DamageSystem
import com.developersyndicate.kxengine.combat.Health
import com.developersyndicate.kxengine.gameplay.Checkpoint
import com.developersyndicate.kxengine.gameplay.CheckpointSystem
import com.developersyndicate.kxengine.graphics.*
import com.developersyndicate.kxengine.graphics.animation.Animator
import com.developersyndicate.kxengine.graphics.animation.SpriteAnimation
import com.developersyndicate.kxengine.input.Input
import com.developersyndicate.kxengine.math.*
import com.developersyndicate.kxengine.graphics.material.TextureMaterial
import com.developersyndicate.kxengine.physics.Body
import com.developersyndicate.kxengine.physics.Collider
import com.developersyndicate.kxengine.physics.Physics
import com.developersyndicate.kxengine.physics.Trigger
import com.developersyndicate.kxengine.physics.TriggerSystem
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
    val playerBody = Body()
    val triggerSystem = TriggerSystem()
    var showColliders = true
    val gravity = -9.8f
    val moveSpeed = 5f
    val jumpForce = 5.5f
    val playerHealth = Health(max = 5)
    val damageSystem = DamageSystem()
    val checkpointSystem = CheckpointSystem()

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
        transform = Transform().apply { scale = Vec3(0.5f, 0.5f, 0.5f) },
        material = playerMaterial
    )

    val enemy = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(1f, 0f, 0f)
        },
        material = enemyMaterial
    )
    val ground = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(0f, -1.5f, 0f)
            scale = Vec3(15f, 0.5f, 1f)
        },
        material = enemyMaterial
    )
    val checkpoint = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(3f, -1f, 0f)
            scale = Vec3(0.4f, 0.4f, 1f)
        },
        material = enemyMaterial
    )

    val pickup = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(2f, -1f, 0f)
            scale = Vec3(0.5f, 0.5f, 1f)
        },
        material = enemyMaterial
    )
    val damageTrigger = Trigger(
        collider = Collider(
            transform = enemy.transform,
            halfSize = Vec2(0.5f, 0.5f)
        ),
        onEnter = {
            damageSystem.apply(
                health = playerHealth,
                damage = Damage(amount = 1)
            )
            println("Player HP: ${playerHealth.current}")
        }
    )

    sprites.add(ground)

    sprites.add(player)
    sprites.add(pickup)
    sprites.add(enemy)
    sprites.add(ground)
    sprites.add(checkpoint)


    camera.target = player.transform

    val pickupTrigger = Trigger(
        collider = Collider(
            transform = pickup.transform,
            halfSize = Vec2(0.25f, 0.25f)
        ),
        onEnter = {
            println("Pickup collected!")
        },
        onExit = {
            println("Left pickup area")
        }
    )
    val checkpointTrigger = Trigger(
        collider = Collider(
            transform = checkpoint.transform,
            halfSize = Vec2(0.2f, 0.2f)
        ),
        onEnter = {
            checkpointSystem.setCheckpoint(
                Checkpoint(checkpoint.transform.position)
            )
        }
    )
    val triggers = listOf(pickupTrigger, damageTrigger, checkpointTrigger)
    // === COLLIDERS ===
    val playerCollider = Collider(
        transform = player.transform,
        halfSize = Vec2(0.25f, 0.25f)
    )

    val wallCollider = Collider(
        transform = enemy.transform,
        halfSize = Vec2(0.1f, 0.1f)
    )
    val debugColliders = listOf(
        playerCollider,
        wallCollider
    )
    val groundCollider = Collider(
        transform = ground.transform,
        halfSize = Vec2(7.5f, 0.25f)
    )

    val speed = 1.0f


    // === GAME LOOP ===
    loop.run { delta ->
        window.pollEvents()
        if (window.shouldClose()) loop.stop()

        // 🔥 SAVE OLD POSITION (PER FRAME)
        val oldPos = player.transform.position

        playerBody.velocity = playerBody.velocity.copy(x = 0f)
        if (Input.isKeyDown(GLFW_KEY_A)) {
            playerBody.velocity = playerBody.velocity.copy(x = -moveSpeed)
        }
        if (Input.isKeyDown(GLFW_KEY_D)) {
            playerBody.velocity = playerBody.velocity.copy(x = moveSpeed)
        }
        if (Input.isKeyPressed(GLFW_KEY_SPACE) && playerBody.grounded) {
            playerBody.velocity = playerBody.velocity.copy(y = jumpForce)
            playerBody.grounded = false
        }
        playerBody.velocity = playerBody.velocity.copy(
            y = playerBody.velocity.y + gravity * delta * playerBody.gravityScale
        )

        player.transform.position = oldPos.copy(
            x = oldPos.x + playerBody.velocity.x * delta
        )

        if (physics.resolve(playerCollider, listOf(wallCollider, groundCollider))) {
            player.transform.position = oldPos
            playerBody.velocity = playerBody.velocity.copy(x = 0f)
        }
        val posAfterX = player.transform.position

        player.transform.position = posAfterX.copy(
            y = posAfterX.y + playerBody.velocity.y * delta
        )

        if (physics.resolve(playerCollider, listOf(wallCollider, groundCollider))) {
            player.transform.position = posAfterX
            playerBody.velocity = playerBody.velocity.copy(y = 0f)
            playerBody.grounded = true
        } else {
            playerBody.grounded = false
        }
        // === COLLISION RESOLUTION ===
        if (physics.resolve(playerCollider, listOf(wallCollider, groundCollider))) {
            player.transform.position = oldPos
        }

        if (Input.isKeyPressed(GLFW_KEY_F1)) {
            showColliders = !showColliders
        }
        triggerSystem.update(
            player = playerCollider,
            triggers = triggers
        )
        playerHealth.update(delta)
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
        if (!playerHealth.isAlive) {
            checkpointSystem.respawn(
                transform = player.transform,
                body = playerBody,
                health = playerHealth
            )
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