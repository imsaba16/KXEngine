package com.developersyndicate.kxengine.physics

class Trigger(
    val collider: Collider,
    val onEnter: () -> Unit = {},
    val onExit: () -> Unit = {}
)