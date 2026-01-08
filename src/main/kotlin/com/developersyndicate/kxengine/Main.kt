package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.ai.EnemyAI
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
import com.developersyndicate.kxengine.physics.*
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

    val physics = Physics()
    val triggerSystem = TriggerSystem()
    val animator = Animator()

    val gravity = -9.8f
    val moveSpeed = 5f
    val jumpForce = 5.5f

    val playerBody = Body()
    val enemyBody = Body()

    val playerHealth = Health(5)
    val damageSystem = DamageSystem()
    val checkpointSystem = CheckpointSystem()

    camera.followSpeed = 4f
    camera.deadZoneWidth = 0.8f
    camera.deadZoneHeight = 0.5f

    val atlasAsset = Assets.atlas(
        path = "assets/atlas.png",
        width = 192f,
        height = 128f,
        columns = 12,
        rows = 8
    )

    val atlas = atlasAsset.atlas
    val atlasTexture = atlasAsset.textureAsset.texture

    val playerMaterial = TextureMaterial(
        texture = atlasTexture,
        region = atlas.region("char_2_5")
    )

    val enemyMaterial = TextureMaterial(
        texture = atlasTexture,
        region = atlas.region("char_5_6")
    )

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

    val player = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(-6f, 0f, 0f)
            scale = Vec3(0.5f, 0.5f, 1f)
        },
        material = playerMaterial
    )

    val enemy = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(6f, 0f, 0f)
            scale = Vec3(0.5f, 0.5f, 1f)
        },
        material = enemyMaterial
    )

    val ground = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(0f, -2f, 0f)
            scale = Vec3(30f, 0.5f, 1f)
        },
        material = enemyMaterial
    )

    val checkpointRenderable = Renderable(
        mesh = quad,
        transform = Transform().apply {
            position = Vec3(-2f, -1.5f, 0f)
            scale = Vec3(0.4f, 0.4f, 1f)
        },
        material = enemyMaterial
    )

    sprites.add(ground)
    sprites.add(player)
    sprites.add(enemy)
    sprites.add(checkpointRenderable)

    camera.target = player.transform

    val playerCollider = Collider(
        transform = player.transform,
        halfSize = Vec2(0.25f, 0.25f)
    )

    val enemyCollider = Collider(
        transform = enemy.transform,
        halfSize = Vec2(0.25f, 0.25f)
    )

    val groundCollider = Collider(
        transform = ground.transform,
        halfSize = Vec2(15f, 0.25f)
    )

    val checkpointTrigger = Trigger(
        collider = Collider(
            transform = checkpointRenderable.transform,
            halfSize = Vec2(0.2f, 0.2f)
        ),
        onEnter = {
            checkpointSystem.setCheckpoint(
                Checkpoint(checkpointRenderable.transform.position)
            )
        }
    )

    val damageTrigger = Trigger(
        collider = enemyCollider,
        onEnter = {
            damageSystem.apply(playerHealth, Damage(1))
        }
    )

    val triggers = listOf(checkpointTrigger, damageTrigger)

    val patrolPoints = listOf(
        Vec3(4f, 0f, 0f),
        Vec3(8f, 0f, 0f)
    )

    val enemyAI = EnemyAI(
        transform = enemy.transform,
        body = enemyBody,
        patrolPoints = patrolPoints
    )

    loop.run { delta ->
        window.pollEvents()
        if (window.shouldClose()) loop.stop()

        playerBody.velocity = playerBody.velocity.copy(x = 0f)

        if (Input.isKeyDown(GLFW_KEY_A)) playerBody.velocity = playerBody.velocity.copy(x = -moveSpeed)
        if (Input.isKeyDown(GLFW_KEY_D)) playerBody.velocity = playerBody.velocity.copy(x = moveSpeed)

        if (Input.isKeyPressed(GLFW_KEY_SPACE) && playerBody.grounded) {
            playerBody.velocity = playerBody.velocity.copy(y = jumpForce)
            playerBody.grounded = false
        }

        playerBody.velocity = playerBody.velocity.copy(
            y = playerBody.velocity.y + gravity * delta
        )

        enemyAI.update(delta, player.transform.position)

        enemyBody.velocity = enemyBody.velocity.copy(
            y = enemyBody.velocity.y + gravity * delta
        )

        val playerOld = player.transform.position
        player.transform.position = playerOld.copy(
            x = playerOld.x + playerBody.velocity.x * delta
        )

        if (physics.resolve(playerCollider, listOf(enemyCollider, groundCollider))) {
            player.transform.position = playerOld
            playerBody.velocity = playerBody.velocity.copy(x = 0f)
        }

        val playerAfterX = player.transform.position
        player.transform.position = playerAfterX.copy(
            y = playerAfterX.y + playerBody.velocity.y * delta
        )

        if (physics.resolve(playerCollider, listOf(enemyCollider, groundCollider))) {
            player.transform.position = playerAfterX
            playerBody.velocity = playerBody.velocity.copy(y = 0f)
            playerBody.grounded = true
        } else {
            playerBody.grounded = false
        }

        val enemyOld = enemy.transform.position
        enemy.transform.position = enemyOld.copy(
            x = enemyOld.x + enemyBody.velocity.x * delta
        )

        if (physics.resolve(enemyCollider, listOf(groundCollider))) {
            enemy.transform.position = enemyOld
            enemyBody.velocity = enemyBody.velocity.copy(x = 0f)
        }

        val enemyAfterX = enemy.transform.position
        enemy.transform.position = enemyAfterX.copy(
            y = enemyAfterX.y + enemyBody.velocity.y * delta
        )

        if (physics.resolve(enemyCollider, listOf(groundCollider))) {
            enemy.transform.position = enemyAfterX
            enemyBody.velocity = enemyBody.velocity.copy(y = 0f)
        }

        triggerSystem.update(playerCollider, triggers)

        playerHealth.update(delta)
        animator.play(
            if (playerBody.velocity.x != 0f) walkAnimation else idleAnimation
        )
        animator.update(delta)
        playerMaterial.region = animator.currentFrame()

        camera.update(delta)

        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        renderer.renderSprites(sprites, camera)
        renderer.render(Scene(), camera, debug = true)

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

    quad.destroy()
    Assets.disposeAll()
    renderer.destroy(TriangleMesh())
    window.destroy()
}