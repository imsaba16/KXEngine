package com.developersyndicate.kxengine

import com.developersyndicate.kxengine.ai.EnemyAI
import com.developersyndicate.kxengine.assets.Assets
import com.developersyndicate.kxengine.combat.*
import com.developersyndicate.kxengine.ecs.World
import com.developersyndicate.kxengine.ecs.components.BodyC
import com.developersyndicate.kxengine.ecs.components.HealthC
import com.developersyndicate.kxengine.ecs.components.TransformC
import com.developersyndicate.kxengine.ecs.systems.PhysicsSystem
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
    val enemyHealth = Health(3)
    val damageSystem = DamageSystem()
    val checkpointSystem = CheckpointSystem()
    val attackSystem = AttackSystem()
    val world = World()
    val physicsSystem = PhysicsSystem(gravity = -9.8f)

    camera.followSpeed = 4f
    camera.deadZoneWidth = 0.8f
    camera.deadZoneHeight = 0.5f
    var facingDir = 1f

    val atlasAsset = Assets.atlas(
        path = "assets/atlas.png",
        width = 192f,
        height = 128f,
        columns = 12,
        rows = 8
    )

    val atlas = atlasAsset.atlas
    val atlasTexture = atlasAsset.textureAsset.texture

    val playerMaterial = TextureMaterial(atlasTexture, atlas.region("char_2_5"))
    val enemyMaterial = TextureMaterial(atlasTexture, atlas.region("char_5_6"))

    val idleAnimation = SpriteAnimation(listOf(atlas.region("char_2_0")), 1f, true)
    val walkAnimation = SpriteAnimation(
        listOf(
            atlas.region("char_2_0"),
            atlas.region("char_2_1"),
            atlas.region("char_2_2")
        ),
        0.15f,
        true
    )
    val enemyDeathAnimation = SpriteAnimation(
        listOf(
            atlas.region("char_5_0"),
            atlas.region("char_5_1"),
            atlas.region("char_5_2")
        ),
        0.12f,
        false
    )

    animator.play(idleAnimation)

    val player = Renderable(
        quad,
        Transform().apply {
            position = Vec3(-6f, 0f, 0f)
            scale = Vec3(0.5f, 0.5f, 1f)
        },
        playerMaterial
    )

    val enemy = Renderable(
        quad,
        Transform().apply {
            position = Vec3(6f, 0f, 0f)
            scale = Vec3(0.5f, 0.5f, 1f)
        },
        enemyMaterial
    )

    val ground = Renderable(
        quad,
        Transform().apply {
            position = Vec3(0f, -2f, 0f)
            scale = Vec3(30f, 0.5f, 1f)
        },
        enemyMaterial
    )

    val checkpointRenderable = Renderable(
        quad,
        Transform().apply {
            position = Vec3(-2f, -1.5f, 0f)
            scale = Vec3(0.4f, 0.4f, 1f)
        },
        enemyMaterial
    )

    val playerEntity = world.createEntity()

    world.add(
        playerEntity,
        TransformC(Vec3(-6f, 0f, 0f))
    )

    world.add(
        playerEntity,
        BodyC()
    )

    world.add(
        playerEntity,
        HealthC(max = 5)
    )

    sprites.addAll(listOf(ground, player, enemy, checkpointRenderable))
    camera.target = player.transform

    val playerCollider = Collider(player.transform, Vec2(0.25f, 0.25f))
    val enemyCollider = Collider(enemy.transform, Vec2(0.25f, 0.25f))
    val groundCollider = Collider(ground.transform, Vec2(15f, 0.25f))

    val checkpointTrigger = Trigger(
        Collider(checkpointRenderable.transform, Vec2(0.2f, 0.2f))
    ) {
        checkpointSystem.setCheckpoint(Checkpoint(checkpointRenderable.transform.position))
    }

    val damageTrigger = Trigger(
        Collider(enemy.transform, Vec2(0.5f, 0.5f))
    ) {
        if (playerBody.hitStun <= 0f) {
            val dir = if (player.transform.position.x < enemy.transform.position.x) -1f else 1f
            damageSystem.apply(
                playerHealth,
                playerBody,
                Damage(1),
                Knockback(Vec2(dir * 4.5f, 2.5f))
            )
            println("Player! HP = ${playerHealth.current}")
            playerBody.hitStun = 0.3f
        }
    }

    val triggers = listOf(checkpointTrigger, damageTrigger)

    val patrolPoints = listOf(Vec3(4f, 0f, 0f), Vec3(8f, 0f, 0f))
    val enemyAI = EnemyAI(enemy.transform, enemyBody, patrolPoints)

    val enemyDeath = EnemyDeath(animator, enemyDeathAnimation, enemyBody)

    loop.run { delta ->
        window.pollEvents()
        if (window.shouldClose()) loop.stop()
        physicsSystem.update(world, delta)

        playerBody.velocity = playerBody.velocity.copy(x = 0f)
        if (playerBody.hitStun > 0f) playerBody.hitStun -= delta

        if (Input.isKeyDown(GLFW_KEY_A)) playerBody.velocity = playerBody.velocity.copy(x = -moveSpeed)
        if (Input.isKeyDown(GLFW_KEY_D)) playerBody.velocity = playerBody.velocity.copy(x = moveSpeed)

        if (Input.isKeyPressed(GLFW_KEY_SPACE) && playerBody.grounded) {
            playerBody.velocity = playerBody.velocity.copy(y = jumpForce)
            playerBody.grounded = false
        }

        playerBody.velocity = playerBody.velocity.copy(y = playerBody.velocity.y + gravity * delta)

        if (enemyDeath.state == DeathState.ALIVE) {
            enemyAI.update(delta, player.transform.position)
        } else {
            enemyBody.velocity = enemyBody.velocity.copy(x = 0f)
        }

        enemyDeath.update(delta)
        if (enemyDeath.isDead()) sprites.remove(enemy)

        enemyBody.velocity = enemyBody.velocity.copy(y = enemyBody.velocity.y + gravity * delta)

        val pOld = player.transform.position
        player.transform.position = pOld.copy(x = pOld.x + playerBody.velocity.x * delta)
        if (physics.resolve(playerCollider, listOf(groundCollider))) {
            player.transform.position = pOld
            playerBody.velocity = playerBody.velocity.copy(x = 0f)
        }

        val pAfterX = player.transform.position
        player.transform.position = pAfterX.copy(y = pAfterX.y + playerBody.velocity.y * delta)
        if (physics.resolve(playerCollider, listOf(groundCollider))) {
            player.transform.position = pAfterX
            playerBody.velocity = playerBody.velocity.copy(y = 0f)
            playerBody.grounded = true
        } else {
            playerBody.grounded = false
        }

        val eOld = enemy.transform.position
        enemy.transform.position = eOld.copy(x = eOld.x + enemyBody.velocity.x * delta)
        if (physics.resolve(enemyCollider, listOf(groundCollider))) {
            enemy.transform.position = eOld
            enemyBody.velocity = enemyBody.velocity.copy(x = 0f)
        }

        val eAfterX = enemy.transform.position
        enemy.transform.position = eAfterX.copy(y = eAfterX.y + enemyBody.velocity.y * delta)
        if (physics.resolve(enemyCollider, listOf(groundCollider))) {
            enemy.transform.position = eAfterX
            enemyBody.velocity = enemyBody.velocity.copy(y = 0f)
        }

        if (Input.isKeyDown(GLFW_KEY_A)) facingDir = -1f
        if (Input.isKeyDown(GLFW_KEY_D)) facingDir = 1f

        if (Input.isKeyPressed(GLFW_KEY_J)) {
            val attackTransform = Transform().apply {
                position = player.transform.position.copy(
                    x = player.transform.position.x + facingDir * 0.6f,
                    y = player.transform.position.y
                )
            }

            val attackCollider = Collider(
                transform = attackTransform,
                halfSize = Vec2(0.6f, 0.4f)
            )

            attackSystem.spawnAttack(
                Attack(
                    collider = attackCollider,
                    damage = 1,
                    knockback = Knockback(
                        Vec2(facingDir * 4.5f, 2.0f)
                    ),
                    lifetime = 0.5f
                )
            )
        }
        attackSystem.update(delta)
        if (enemyDeath.state == DeathState.ALIVE) {
            attackSystem.checkHits(enemyCollider) { attack ->
                enemyHealth.damage(attack.damage)

                enemyBody.velocity += attack.knockback.force
                println("Enemy HP: ${enemyHealth.current}")
                if (!enemyHealth.isAlive) {
                    enemyDeath.trigger()
                }
            }
        }

        triggerSystem.update(playerCollider, triggers)

        playerHealth.update(delta)
        animator.play(if (playerBody.velocity.x != 0f) walkAnimation else idleAnimation)
        animator.update(delta)
        playerMaterial.region = animator.currentFrame()

        camera.update(delta)

        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        renderer.renderSprites(sprites, camera)
        renderer.render(Scene(), camera, true)

        if (!playerHealth.isAlive) {
            checkpointSystem.respawn(player.transform, playerBody, playerHealth)
            enemyHealth.current = enemyHealth.max
            if (!sprites.contains(enemy)) sprites.add(enemy)
        }

        Input.endFrame()
        window.swapBuffers()
    }

    quad.destroy()
    Assets.disposeAll()
    renderer.destroy(TriangleMesh())
    window.destroy()
}