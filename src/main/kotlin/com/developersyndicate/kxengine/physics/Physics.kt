package com.developersyndicate.kxengine.physics

class Physics {

    fun resolve(
        dynamic: Collider,
        statics: List<Collider>
    ): Boolean {
        val dynBox = dynamic.aabb()

        for (other in statics) {
            if (dynBox.overlaps(other.aabb())) {
                return true
            }
        }
        return false
    }
}