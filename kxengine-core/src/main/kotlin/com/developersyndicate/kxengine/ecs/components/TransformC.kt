package com.developersyndicate.kxengine.ecs.components

import com.developersyndicate.kxengine.ecs.Component
import com.developersyndicate.kxengine.math.Vec3

data class TransformC(
    var position: Vec3
) : Component