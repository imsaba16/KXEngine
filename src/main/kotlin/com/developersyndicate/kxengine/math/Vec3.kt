package com.developersyndicate.kxengine.math

data class Vec3(val x: Float, val y: Float, val z: Float) {
    companion object {
        val ZERO = Vec3(0f, 0f, 0f)
        val ONE = Vec3(1f, 1f, 1f)
    }
}
