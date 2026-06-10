package com.developersyndicate.kxengine.graphics.animation

import com.developersyndicate.kxengine.graphics.atlas.AtlasRegion

class AnimatorController {
    private val states = mutableMapOf<String, AnimationState>()
    private val parameters = mutableMapOf<String, Any>()
    private val triggers = mutableSetOf<String>()

    var currentState: AnimationState? = null
        private set

    fun addState(state: AnimationState) {
        states[state.name] = state
        if (currentState == null) {
            currentState = state
        }
    }

    fun setFloat(name: String, value: Float) {
        parameters[name] = value
    }

    fun setBoolean(name: String, value: Boolean) {
        parameters[name] = value
    }

    fun setTrigger(name: String) {
        triggers.add(name)
    }

    fun getParameter(name: String): Any? {
        if (triggers.contains(name)) return true
        return parameters[name]
    }

    fun update(delta: Float) {
        val current = currentState ?: return

        var takenTransition: Transition? = null
        for (transition in current.transitions) {
            if (transition.hasExitTime && !current.isFinished()) {
                continue
            }

            var conditionsMet = true
            for (cond in transition.conditions) {
                if (!evaluateCondition(cond)) {
                    conditionsMet = false
                    break
                }
            }

            if (conditionsMet) {
                takenTransition = transition
                break
            }
        }

        if (takenTransition != null) {
            // Consume triggers
            for (cond in takenTransition.conditions) {
                if (triggers.contains(cond.parameterName)) {
                    triggers.remove(cond.parameterName)
                }
            }

            val nextState = states[takenTransition.targetState]
            if (nextState != null) {
                currentState = nextState
                currentState?.reset()
            }
        }

        currentState?.update(delta, this)
    }

    fun currentFrame(): AtlasRegion {
        val current = currentState ?: error("AnimatorController has no state active")
        return current.currentFrame(this)
    }

    private fun evaluateCondition(cond: TransitionCondition): Boolean {
        val paramName = cond.parameterName
        val paramVal = if (triggers.contains(paramName)) true else parameters[paramName] ?: return false

        return when (cond.type) {
            ConditionType.TRUE -> {
                if (paramVal is Boolean) paramVal else false
            }
            ConditionType.FALSE -> {
                if (paramVal is Boolean) !paramVal else false
            }
            ConditionType.GREATER -> {
                val v1 = (paramVal as? Number)?.toFloat() ?: 0f
                val v2 = (cond.value as? Number)?.toFloat() ?: 0f
                v1 > v2
            }
            ConditionType.LESS -> {
                val v1 = (paramVal as? Number)?.toFloat() ?: 0f
                val v2 = (cond.value as? Number)?.toFloat() ?: 0f
                v1 < v2
            }
            ConditionType.EQUALS -> {
                paramVal == cond.value
            }
            ConditionType.NOT_EQUALS -> {
                paramVal != cond.value
            }
        }
    }
}
