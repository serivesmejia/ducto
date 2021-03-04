package com.github.serivesmejia.ducto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

open class Ducto<I : Any, O : Any> {

    internal var parentScope: DuctoScope<O, I, out Any>? = null

    internal var hasDeclaredFinally = false

    @Suppress("UNCHECKED_CAST")
    fun process(input: I) : O {
        if(!hasDeclaredFinally) {
            throw IllegalStateException("Can't process before having a \"finally\" stage")
        }

        parentScope?.let {
            return it.execute(input) as O
        }

        throw IllegalStateException("Can't process before having a \"first\" stage")
    }

    fun <o : Any> first(stage: Stage<I, o>): DuctoScope<O, I, o> {
        val scope = DuctoScope<O, I, o>(this)
        scope.stage = stage

        parentScope = scope
        return scope
    }

}