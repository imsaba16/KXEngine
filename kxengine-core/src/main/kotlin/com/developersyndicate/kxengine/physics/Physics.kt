package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.math.Vec2
import com.developersyndicate.kxengine.math.Vec3
import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs

class Physics {

    fun sweptAABB(
        dynamic: Collider,
        velocity: Vec2,
        static: Collider
    ): CollisionResult {
        val dynBox = dynamic.aabb()
        val statBox = static.aabb()

        // 1. Check if they already overlap
        if (dynBox.overlaps(statBox)) {
            val overlapX = min(dynBox.max.x, statBox.max.x) - max(dynBox.min.x, statBox.min.x)
            val overlapY = min(dynBox.max.y, statBox.max.y) - max(dynBox.min.y, statBox.min.y)
            return if (overlapX < overlapY) {
                val normalX = if (dynamic.transform.position.x < static.transform.position.x) -1f else 1f
                CollisionResult(true, 0f, Vec2(normalX, 0f))
            } else {
                val normalY = if (dynamic.transform.position.y < static.transform.position.y) -1f else 1f
                CollisionResult(true, 0f, Vec2(0f, normalY))
            }
        }

        // 2. Perform Swept AABB
        val min = Vec2(statBox.min.x - dynamic.halfSize.x, statBox.min.y - dynamic.halfSize.y)
        val max = Vec2(statBox.max.x + dynamic.halfSize.x, statBox.max.y + dynamic.halfSize.y)

        val start = Vec2(dynamic.transform.position.x, dynamic.transform.position.y)

        var xEntry = 0f
        var yEntry = 0f
        var xExit = 0f
        var yExit = 0f

        if (velocity.x > 0f) {
            xEntry = min.x - start.x
            xExit = max.x - start.x
        } else {
            xEntry = max.x - start.x
            xExit = min.x - start.x
        }

        if (velocity.y > 0f) {
            yEntry = min.y - start.y
            yExit = max.y - start.y
        } else {
            yEntry = max.y - start.y
            yExit = min.y - start.y
        }

        val txEntry = if (velocity.x == 0f) {
            if (start.x in min.x..max.x) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
        } else {
            xEntry / velocity.x
        }
        val txExit = if (velocity.x == 0f) {
            Float.POSITIVE_INFINITY
        } else {
            xExit / velocity.x
        }

        val tyEntry = if (velocity.y == 0f) {
            if (start.y in min.y..max.y) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
        } else {
            yEntry / velocity.y
        }
        val tyExit = if (velocity.y == 0f) {
            Float.POSITIVE_INFINITY
        } else {
            yExit / velocity.y
        }

        val tEntry = max(txEntry, tyEntry)
        val tExit = min(txExit, tyExit)

        // No collision checks
        if (tEntry > tExit || (txEntry < 0f && tyEntry < 0f) || txEntry > 1f || tyEntry > 1f) {
            return CollisionResult.NONE
        }

        // Collision normal calculation
        val normal = if (txEntry > tyEntry) {
            if (xEntry < 0f) Vec2(1f, 0f) else Vec2(-1f, 0f)
        } else {
            if (yEntry < 0f) Vec2(0f, 1f) else Vec2(0f, -1f)
        }

        return CollisionResult(true, tEntry, normal)
    }

    fun resolveSliding(
        dynamic: Collider,
        velocity: Vec2,
        statics: List<Collider>,
        delta: Float
    ): ResolutionResult {
        var remainingVel = velocity
        var currentPos = Vec2(dynamic.transform.position.x, dynamic.transform.position.y)
        var grounded = false
        var hitWall = false

        for (iteration in 0 until 3) {
            val sweepVel = remainingVel * delta
            if (sweepVel.x == 0f && sweepVel.y == 0f) break

            var closestResult = CollisionResult.NONE
            var closestCollider: Collider? = null

            for (static in statics) {
                if (!dynamic.canCollideWith(static)) continue

                val result = sweptAABB(dynamic, sweepVel, static)
                if (result.collided && result.time < closestResult.time) {
                    closestResult = result
                    closestCollider = static
                }
            }

            if (closestResult.collided) {
                val backOff = 0.001f
                val moveFraction = max(0f, closestResult.time - backOff)
                
                currentPos = currentPos + sweepVel * moveFraction
                dynamic.transform.position = Vec3(currentPos.x, currentPos.y, dynamic.transform.position.z)

                if (closestResult.normal.y > 0.7f) {
                    grounded = true
                }
                if (abs(closestResult.normal.x) > 0.7f) {
                    hitWall = true
                }

                // Project remaining velocity along the collision plane (slide)
                val dot = remainingVel.x * closestResult.normal.x + remainingVel.y * closestResult.normal.y
                remainingVel = remainingVel - closestResult.normal * dot
            } else {
                currentPos = currentPos + sweepVel
                dynamic.transform.position = Vec3(currentPos.x, currentPos.y, dynamic.transform.position.z)
                break
            }
        }

        return ResolutionResult(remainingVel, grounded, hitWall)
    }

    fun raycast(
        ray: Ray,
        maxDistance: Float,
        statics: List<Collider>
    ): RaycastHit {
        var closestHit = RaycastHit.NONE

        for (static in statics) {
            val hit = intersectRayAABB(ray, static, maxDistance)
            if (hit.hit && hit.distance < closestHit.distance) {
                closestHit = hit
            }
        }

        return closestHit
    }

    private fun intersectRayAABB(ray: Ray, static: Collider, maxDistance: Float): RaycastHit {
        val aabb = static.aabb()
        var tMin = 0.0f
        var tMax = maxDistance

        // X Axis
        if (abs(ray.direction.x) < 1e-6f) {
            if (ray.origin.x < aabb.min.x || ray.origin.x > aabb.max.x) {
                return RaycastHit.NONE
            }
        } else {
            val invDir = 1.0f / ray.direction.x
            var t1 = (aabb.min.x - ray.origin.x) * invDir
            var t2 = (aabb.max.x - ray.origin.x) * invDir
            if (t1 > t2) {
                val temp = t1; t1 = t2; t2 = temp
            }
            tMin = max(tMin, t1)
            tMax = min(tMax, t2)
            if (tMin > tMax) return RaycastHit.NONE
        }

        // Y Axis
        if (abs(ray.direction.y) < 1e-6f) {
            if (ray.origin.y < aabb.min.y || ray.origin.y > aabb.max.y) {
                return RaycastHit.NONE
            }
        } else {
            val invDir = 1.0f / ray.direction.y
            var t1 = (aabb.min.y - ray.origin.y) * invDir
            var t2 = (aabb.max.y - ray.origin.y) * invDir
            if (t1 > t2) {
                val temp = t1; t1 = t2; t2 = temp
            }
            tMin = max(tMin, t1)
            tMax = min(tMax, t2)
            if (tMin > tMax) return RaycastHit.NONE
        }

        val hitPoint = ray.origin + ray.direction * tMin

        // Determine normal with minor epsilon bounds
        var normal = Vec2(0f, 0f)
        val bias = 1e-3f
        if (abs(hitPoint.x - aabb.min.x) < bias) normal = Vec2(-1f, 0f)
        else if (abs(hitPoint.x - aabb.max.x) < bias) normal = Vec2(1f, 0f)
        else if (abs(hitPoint.y - aabb.min.y) < bias) normal = Vec2(0f, -1f)
        else if (abs(hitPoint.y - aabb.max.y) < bias) normal = Vec2(0f, 1f)

        return RaycastHit(true, hitPoint, normal, tMin, static)
    }

    // Retained for simple non-swept overlap verification
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