package com.developersyndicate.kxengine.ecs.components

import com.developersyndicate.kxengine.ecs.Component

data class HealthC(
    val max: Int,
    var current: Int = max
) : Component