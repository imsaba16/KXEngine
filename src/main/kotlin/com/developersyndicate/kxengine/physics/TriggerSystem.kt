package com.developersyndicate.kxengine.physics

class TriggerSystem {

    private val active = mutableSetOf<Trigger>()

    fun update(
        player: Collider,
        triggers: List<Trigger>
    ) {
        for (trigger in triggers) {
            val overlapping = player.aabb().overlaps(trigger.collider.aabb())

            if (overlapping && trigger !in active) {
                active.add(trigger)
                trigger.onEnter()
            }

            if (!overlapping && trigger in active) {
                active.remove(trigger)
                trigger.onExit()
            }
        }
    }
}