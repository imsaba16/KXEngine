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
import com.developersyndicate.kxengine.audio.Sound
import com.developersyndicate.kxengine.audio.SoundSource
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

class DemoGame : Game {
    private lateinit var engine: KXEngine
    private lateinit var camera: Camera
    private lateinit var quad: QuadMesh
    private val sprites = mutableListOf<Renderable>()

    private val physics = Physics()
    private val triggerSystem = TriggerSystem()
    private val playerAnimator = Animator()
    private val enemyAnimator = Animator()

    private val gravity = -9.8f
    private val moveSpeed = 5f
    private val jumpForce = 5.5f

    private val playerBody = Body()
    private val enemyBody = Body()

    private lateinit var playerHealth: Health
    private lateinit var enemyHealth: Health
    private val damageSystem = DamageSystem()
    private lateinit var checkpointSystem: CheckpointSystem
    private val attackSystem = AttackSystem()
    private val world = World()
    private val physicsSystem = PhysicsSystem(gravity = -9.8f)

    private var facingDir = 1f

    private lateinit var player: Renderable
    private lateinit var enemy: Renderable
    private lateinit var ground: Renderable
    private lateinit var checkpointRenderable: Renderable

    private lateinit var playerCollider: Collider
    private lateinit var enemyCollider: Collider
    private lateinit var groundCollider: Collider

    private lateinit var fbo: Framebuffer

    private lateinit var jumpSound: Sound
    private lateinit var jumpSource: SoundSource
    private lateinit var hitSound: Sound
    private lateinit var hitSource: SoundSource

    private lateinit var checkpointTrigger: Trigger
    private lateinit var damageTrigger: Trigger
    private lateinit var triggers: List<Trigger>

    private lateinit var enemyAI: EnemyAI
    private lateinit var enemyDeath: EnemyDeath

    private lateinit var idleAnimation: SpriteAnimation
    private lateinit var walkAnimation: SpriteAnimation
    private lateinit var enemyDeathAnimation: SpriteAnimation

    private lateinit var playerMaterial: TextureMaterial
    private lateinit var enemyMaterial: TextureMaterial
    private lateinit var lantern: PointLight2D

    override fun init(engine: KXEngine) {
        this.engine = engine
        
        lantern = PointLight2D(
            position = Vec2(-6f, 0f),
            color = Color(1.0f, 0.85f, 0.6f),
            radius = 5.0f,
            intensity = 1.0f
        )
        engine.renderer.pointLight = lantern
        engine.renderer.ambientColor = Color(0.12f, 0.12f, 0.16f)

        fbo = Framebuffer(engine.width, engine.height)

        camera = Camera(2f, 2f)
        camera.followSpeed = 4f
        camera.deadZoneWidth = 0.8f
        camera.deadZoneHeight = 0.5f

        quad = QuadMesh()

        val atlasAsset = Assets.atlas(
            path = "assets/atlas.png",
            width = 192f,
            height = 128f,
            columns = 12,
            rows = 8
        )

        val atlas = atlasAsset.atlas
        val atlasTexture = atlasAsset.textureAsset.texture

        playerMaterial = TextureMaterial(atlasTexture, atlas.region("char_2_5"))
        enemyMaterial = TextureMaterial(atlasTexture, atlas.region("char_5_6"))

        idleAnimation = SpriteAnimation(listOf(atlas.region("char_2_0")), 1f, true)
        walkAnimation = SpriteAnimation(
            listOf(
                atlas.region("char_2_0"),
                atlas.region("char_2_1"),
                atlas.region("char_2_2")
            ),
            0.15f,
            true
        )
        enemyDeathAnimation = SpriteAnimation(
            listOf(
                atlas.region("char_5_0"),
                atlas.region("char_5_1"),
                atlas.region("char_5_2")
            ),
            0.12f,
            false
        )

        playerAnimator.play(idleAnimation)

        player = Renderable(
            quad,
            Transform().apply {
                position = Vec3(-6f, 0f, 0f)
                scale = Vec3(0.5f, 0.5f, 1f)
            },
            playerMaterial,
            zIndex = 3
        )

        enemy = Renderable(
            quad,
            Transform().apply {
                position = Vec3(6f, 0f, 0f)
                scale = Vec3(0.5f, 0.5f, 1f)
            },
            enemyMaterial,
            zIndex = 2
        )

        ground = Renderable(
            quad,
            Transform().apply {
                position = Vec3(0f, -2f, 0f)
                scale = Vec3(30f, 0.5f, 1f)
            },
            enemyMaterial,
            zIndex = 0
        )

        checkpointRenderable = Renderable(
            quad,
            Transform().apply {
                position = Vec3(-2f, -1.5f, 0f)
                scale = Vec3(0.4f, 0.4f, 1f)
            },
            enemyMaterial,
            zIndex = 1
        )

        val playerEntity = world.createEntity()
        world.add(playerEntity, TransformC(Vec3(-6f, 0f, 0f)))
        world.add(playerEntity, BodyC())
        world.add(playerEntity, HealthC(max = 5))

        sprites.addAll(listOf(ground, player, enemy, checkpointRenderable))
        camera.target = player.transform

        playerCollider = Collider(player.transform, Vec2(0.25f, 0.25f), collisionLayer = 1, collisionMask = -1)
        enemyCollider = Collider(enemy.transform, Vec2(0.25f, 0.25f), collisionLayer = 2, collisionMask = 5) // collides with Player (1) and Ground (4)
        groundCollider = Collider(ground.transform, Vec2(15f, 0.25f), collisionLayer = 4, collisionMask = -1)

        playerHealth = Health(5)
        enemyHealth = Health(3)
        checkpointSystem = CheckpointSystem(defaultSpawn = player.transform.position.copy())

        checkpointTrigger = Trigger(
            Collider(checkpointRenderable.transform, Vec2(0.2f, 0.2f))
        ) {
            checkpointSystem.setCheckpoint(Checkpoint(checkpointRenderable.transform.position))
        }

        damageTrigger = Trigger(
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
                hitSource.play(hitSound)
            }
        }

        triggers = listOf(checkpointTrigger, damageTrigger)

        val patrolPoints = listOf(Vec3(4f, 0f, 0f), Vec3(8f, 0f, 0f))
        enemyAI = EnemyAI(enemy.transform, enemyBody, patrolPoints)

        enemyDeath = EnemyDeath(enemyAnimator, enemyDeathAnimation, enemyBody)

        jumpSound = Sound("assets/jump.wav")
        jumpSource = SoundSource()
        hitSound = Sound("assets/hit.wav")
        hitSource = SoundSource()
    }

    override fun update(fixedDelta: Float) {
        physicsSystem.update(world, fixedDelta)

        playerBody.velocity = playerBody.velocity.copy(x = 0f)
        if (playerBody.hitStun > 0f) playerBody.hitStun -= fixedDelta

        if (Input.isKeyDown(GLFW_KEY_A)) playerBody.velocity = playerBody.velocity.copy(x = -moveSpeed)
        if (Input.isKeyDown(GLFW_KEY_D)) playerBody.velocity = playerBody.velocity.copy(x = moveSpeed)

        if (Input.isKeyPressed(GLFW_KEY_SPACE) && playerBody.grounded) {
            playerBody.velocity = playerBody.velocity.copy(y = jumpForce)
            playerBody.grounded = false
            jumpSource.play(jumpSound)
        }

        playerBody.velocity = playerBody.velocity.copy(y = playerBody.velocity.y + gravity * fixedDelta)

        if (enemyDeath.state == DeathState.ALIVE) {
            enemyAI.update(fixedDelta, player.transform.position)
        } else {
            enemyBody.velocity = enemyBody.velocity.copy(x = 0f)
        }

        enemyDeath.update(fixedDelta)
        if (enemyDeath.isDead()) sprites.remove(enemy)

        enemyBody.velocity = enemyBody.velocity.copy(y = enemyBody.velocity.y + gravity * fixedDelta)

        val playerRes = physics.resolveSliding(playerCollider, playerBody.velocity, listOf(groundCollider), fixedDelta)
        playerBody.velocity = playerRes.velocity
        playerBody.grounded = playerRes.grounded

        val enemyRes = physics.resolveSliding(enemyCollider, enemyBody.velocity, listOf(groundCollider), fixedDelta)
        enemyBody.velocity = enemyRes.velocity

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
        attackSystem.update(fixedDelta)
        if (enemyDeath.state == DeathState.ALIVE) {
            attackSystem.checkHits(enemyCollider) { attack ->
                enemyHealth.damage(attack.damage)

                enemyBody.velocity += attack.knockback.force
                println("Enemy HP: ${enemyHealth.current}")
                hitSource.play(hitSound)
                if (!enemyHealth.isAlive) {
                    enemyDeath.trigger()
                }
            }
        }

        triggerSystem.update(playerCollider, triggers)

        playerHealth.update(fixedDelta)
        playerAnimator.play(if (playerBody.velocity.x != 0f) walkAnimation else idleAnimation)
        playerAnimator.update(fixedDelta)
        playerMaterial.region = playerAnimator.currentFrame()

        if (enemyDeath.state == DeathState.DYING) {
            enemyAnimator.update(fixedDelta)
            enemyMaterial.region = enemyAnimator.currentFrame()
        }

        camera.update(fixedDelta)
        lantern.position = Vec2(player.transform.position.x, player.transform.position.y)

        if (!playerHealth.isAlive) {
            checkpointSystem.respawn(player.transform, playerBody, playerHealth)
            enemyHealth.current = enemyHealth.max
            if (!sprites.contains(enemy)) sprites.add(enemy)
        }
    }

    override fun render(delta: Float) {
        fbo.bind()

        glClearColor(0.05f, 0.05f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        engine.renderer.renderSprites(sprites, camera)
        engine.renderer.render(Scene(), camera, true)

        fbo.unbind(engine.width, engine.height)

        // Draw offscreen FBO texture to the screen with a vignette shader effect
        engine.renderer.renderFramebuffer(
            fbo = fbo,
            windowWidth = engine.width,
            windowHeight = engine.height,
            grayscale = false,
            vignetteStrength = 1.0f
        )
    }

    override fun dispose() {
        jumpSound.destroy()
        jumpSource.destroy()
        hitSound.destroy()
        hitSource.destroy()
        fbo.destroy()
        quad.destroy()
        Assets.disposeAll()
        engine.renderer.destroy(TriangleMesh())
    }
}

fun main() {
    EngineConfig.load()
    val game = DemoGame()
    val engine = KXEngine(
        game = game,
        width = EngineConfig.windowWidth,
        height = EngineConfig.windowHeight,
        title = EngineConfig.windowTitle
    )
    engine.start()
}