package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion
import com.developersyndicate.kxengine.graphics.batch.SpriteBatch
import com.developersyndicate.kxengine.math.Vec2
import org.lwjgl.opengl.GL11.*
import kotlin.random.Random

class ParticleEmitter(
    var position: Vec2,
    val maxParticles: Int = 500
) {
    enum class BlendMode {
        ALPHA, ADDITIVE
    }

    var active = true
    var emissionRate = 100f // particles per second
    var blendMode = BlendMode.ALPHA

    // Configuration templates
    var minLife = 1.0f
    var maxLife = 2.0f

    var minSpeed = 1.0f
    var maxSpeed = 3.0f

    var minAngle = 0f // Degrees
    var maxAngle = 360f

    var gravityY = 0f // Pull force on y-axis

    var startScale = 0.2f
    var endScale = 0.0f

    var startColor = Color.WHITE
    var endColor = Color.WHITE

    var minAngularVelocity = -90f // Degrees per second
    var maxAngularVelocity = 90f

    var spawnRadius = 0f

    var texture: Texture? = null
    var region: AtlasRegion? = null

    private val pool = Array(maxParticles) { Particle() }
    private var spawnTimer = 0f

    class Particle {
        var active = false
        var x = 0f
        var y = 0f
        var vx = 0f
        var vy = 0f
        var life = 0f
        var maxLife = 0f
        var rotation = 0f
        var angularVelocity = 0f
        var scale = 0f
        var r = 1f
        var g = 1f
        var b = 1f
        var a = 1f
    }

    fun update(dt: Float) {
        // 1. Update active particles
        for (p in pool) {
            if (!p.active) continue

            p.life -= dt
            if (p.life <= 0f) {
                p.active = false
                continue
            }

            p.vy += gravityY * dt
            p.x += p.vx * dt
            p.y += p.vy * dt

            p.rotation += p.angularVelocity * dt

            val t = ((p.maxLife - p.life) / p.maxLife).coerceIn(0f, 1f)
            p.scale = startScale + (endScale - startScale) * t

            p.r = startColor.r + (endColor.r - startColor.r) * t
            p.g = startColor.g + (endColor.g - startColor.g) * t
            p.b = startColor.b + (endColor.b - startColor.b) * t
            p.a = startColor.a + (endColor.a - startColor.a) * t
        }

        // 2. Spawn new particles over time
        if (active && emissionRate > 0f) {
            spawnTimer += dt
            val timeBetweenSpawns = 1f / emissionRate
            while (spawnTimer >= timeBetweenSpawns) {
                spawnTimer -= timeBetweenSpawns
                spawnParticle()
            }
        }
    }

    fun burst(count: Int) {
        var spawned = 0
        for (p in pool) {
            if (spawned >= count) break
            if (!p.active) {
                spawnParticleDirect(p)
                spawned++
            }
        }
    }

    private fun spawnParticle() {
        val p = pool.firstOrNull { !it.active } ?: return
        spawnParticleDirect(p)
    }

    private fun spawnParticleDirect(p: Particle) {
        p.active = true
        p.x = position.x
        p.y = position.y

        if (spawnRadius > 0f) {
            val r = Random.nextFloat() * spawnRadius
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            p.x += (r * Math.cos(angle.toDouble())).toFloat()
            p.y += (r * Math.sin(angle.toDouble())).toFloat()
        }

        val life = minLife + Random.nextFloat() * (maxLife - minLife)
        p.life = life
        p.maxLife = life

        val speed = minSpeed + Random.nextFloat() * (maxSpeed - minSpeed)
        val angleDeg = minAngle + Random.nextFloat() * (maxAngle - minAngle)
        val angleRad = Math.toRadians(angleDeg.toDouble())

        p.vx = (speed * Math.cos(angleRad)).toFloat()
        p.vy = (speed * Math.sin(angleRad)).toFloat()

        p.rotation = Random.nextFloat() * Math.PI.toFloat() * 2f
        val angVelDeg = minAngularVelocity + Random.nextFloat() * (maxAngularVelocity - minAngularVelocity)
        p.angularVelocity = Math.toRadians(angVelDeg.toDouble()).toFloat()

        p.scale = startScale
        p.r = startColor.r
        p.g = startColor.g
        p.b = startColor.b
        p.a = startColor.a
    }

    fun draw(batch: SpriteBatch) {
        val tex = texture ?: return
        val reg = region ?: return

        val hasActiveParticles = pool.any { it.active }
        if (!hasActiveParticles) return

        if (blendMode == BlendMode.ADDITIVE) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE)
        }

        batch.begin(tex)
        for (p in pool) {
            if (!p.active) continue
            batch.draw(
                x = p.x,
                y = p.y,
                rotation = p.rotation,
                scaleX = p.scale,
                scaleY = p.scale,
                region = reg,
                color = floatArrayOf(p.r, p.g, p.b, p.a)
            )
        }
        batch.end()

        if (blendMode == BlendMode.ADDITIVE) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    companion object {
        fun createFire(position: Vec2, texture: Texture, region: AtlasRegion): ParticleEmitter {
            return ParticleEmitter(position).apply {
                this.texture = texture
                this.region = region
                emissionRate = 60f
                blendMode = BlendMode.ADDITIVE
                minLife = 0.5f
                maxLife = 1.0f
                minSpeed = 0.8f
                maxSpeed = 1.8f
                minAngle = 80f
                maxAngle = 100f
                gravityY = 1.5f // Float upwards
                startScale = 0.4f
                endScale = 0.1f
                startColor = Color(1.0f, 0.5f, 0.1f, 0.8f) // Orange
                endColor = Color(0.8f, 0.1f, 0.0f, 0.0f)   // Fade to dark red/transparent
                minAngularVelocity = -180f
                maxAngularVelocity = 180f
                spawnRadius = 0.15f
            }
        }

        fun createSmoke(position: Vec2, texture: Texture, region: AtlasRegion): ParticleEmitter {
            return ParticleEmitter(position).apply {
                this.texture = texture
                this.region = region
                emissionRate = 20f
                blendMode = BlendMode.ALPHA
                minLife = 1.5f
                maxLife = 2.5f
                minSpeed = 0.3f
                maxSpeed = 0.7f
                minAngle = 85f
                maxAngle = 95f
                gravityY = 0.2f
                startScale = 0.3f
                endScale = 0.8f
                startColor = Color(0.4f, 0.4f, 0.4f, 0.4f) // Semi-transparent grey
                endColor = Color(0.2f, 0.2f, 0.2f, 0.0f)   // Fade out
                minAngularVelocity = -45f
                maxAngularVelocity = 45f
                spawnRadius = 0.2f
            }
        }

        fun createExplosion(position: Vec2, texture: Texture, region: AtlasRegion): ParticleEmitter {
            return ParticleEmitter(position).apply {
                this.texture = texture
                this.region = region
                emissionRate = 0f // One-shot burst
                blendMode = BlendMode.ADDITIVE
                minLife = 0.3f
                maxLife = 0.6f
                minSpeed = 2.0f
                maxSpeed = 4.5f
                minAngle = 0f
                maxAngle = 360f
                gravityY = -4.0f // Gravity pulls down sparks
                startScale = 0.25f
                endScale = 0.05f
                startColor = Color(1.0f, 0.9f, 0.4f, 1.0f) // Bright yellow
                endColor = Color(0.9f, 0.2f, 0.1f, 0.0f)   // Fade to red/transparent
                minAngularVelocity = -360f
                maxAngularVelocity = 360f
                spawnRadius = 0.05f
            }
        }
    }
}
