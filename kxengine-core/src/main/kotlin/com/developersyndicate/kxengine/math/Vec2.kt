package com.developersyndicate.kxengine.math

data class Vec2(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: Vec2): Vec2 =
        Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2): Vec2 =
        Vec2(x - other.x, y - other.y)

    operator fun times(scalar: Float): Vec2 =
        Vec2(x * scalar, y * scalar)
}