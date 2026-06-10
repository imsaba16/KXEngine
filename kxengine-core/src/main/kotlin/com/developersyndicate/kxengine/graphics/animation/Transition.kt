package com.developersyndicate.kxengine.graphics.animation

enum class ConditionType {
    GREATER, LESS, EQUALS, NOT_EQUALS, TRUE, FALSE
}

class TransitionCondition(
    val parameterName: String,
    val type: ConditionType,
    val value: Any? = null
)

class Transition(
    val targetState: String,
    val conditions: List<TransitionCondition>,
    val hasExitTime: Boolean = false,
    val exitTime: Float = 1.0f // Normalized time (1.0 = end of animation)
)
