package com.github.serivesmejia.ducto

class Ducto<I : Any, O : Any> {

    private var parentScope: DuctoScope<O, out Any, out Any>? = null;

    internal var hasDeclaredFinally = false

    fun process(input: I) : O {
        if(!hasDeclaredFinally) {
            throw IllegalStateException("Can't process before having a \"finally\" stage")
        }

        parentScope?.let {
            return it.internalExecute(input) as O
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