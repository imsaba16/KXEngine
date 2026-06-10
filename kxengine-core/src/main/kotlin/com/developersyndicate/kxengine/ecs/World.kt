package com.developersyndicate.kxengine.ecs

class World {

    private var nextEntity = 0

    private val components =
        mutableMapOf<Class<*>, MutableMap<Entity, Component>>()

    fun createEntity(): Entity = nextEntity++

    fun <T : Component> add(entity: Entity, component: T) {
        val map = components.getOrPut(component::class.java) {
            mutableMapOf()
        }
        map[entity] = component
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> get(entity: Entity, type: Class<T>): T? {
        return components[type]?.get(entity) as? T
    }

    fun <A : Component, B : Component> forEach(
        a: Class<A>,
        b: Class<B>,
        block: (Entity, A, B) -> Unit
    ) {
        val mapA = components[a] ?: return
        val mapB = components[b] ?: return

        for (entity in mapA.keys) {
            val compA = mapA[entity] as? A ?: continue
            val compB = mapB[entity] as? B ?: continue
            block(entity, compA, compB)
        }
    }
}