package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.*
import kotlin.math.*
import kotlin.random.Random

class Camera(
    width: Float,
    height: Float
) {
    var position = Vec3.ZERO
    var target: Transform? = null
    var followSpeed = 6f
    var deadZoneWidth = 0.6f
    var deadZoneHeight = 0.4f

    private var shakeTime = 0f
    private var shakeDuration = 0f
    private var shakeStrength = 0f
    private var shakeOffset = Vec3.ZERO
    private val projection = Mat4.orthographic(
        -width / 2f,
        width / 2f,
        -height / 2f,
        height / 2f
    )

    fun update(delta: Float) {
        target?.let { t ->
            val tp = t.position
            val localX = tp.x - position.x
            val localY = tp.y - position.y

            val halfW = deadZoneWidth / 2f
            val halfH = deadZoneHeight / 2f

            var moveX = 0f
            var moveY = 0f

            if (localX > halfW) moveX = localX - halfW
            else if (localX < -halfW) moveX = localX + halfW

            if (localY > halfH) moveY = localY - halfH
            else if (localY < -halfH) moveY = localY + halfH

            position = Vec3(
                position.x + moveX * followSpeed * delta,
                position.y + moveY * followSpeed * delta,
                position.z
            )
        }

        if (shakeTime > 0f) {
            shakeTime -= delta

            val progress = shakeTime / shakeDuration
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val strength = shakeStrength * progress

            shakeOffset = Vec3(
                cos(angle) * strength,
                sin(angle) * strength,
                0f
            )
        } else {
            shakeOffset = Vec3.ZERO
        }
    }

    fun shake(strength: Float, duration: Float) {
        shakeStrength = strength
        shakeDuration = duration
        shakeTime = duration
    }

    private fun view(): Mat4 {
        val finalPos = Vec3(
            position.x + shakeOffset.x,
            position.y + shakeOffset.y,
            position.z
        )

        return Mat4.translation(
            Vec3(-finalPos.x, -finalPos.y, -finalPos.z)
        )
    }

    fun matrix(): Mat4 {
        return projection * view()
    }
}
