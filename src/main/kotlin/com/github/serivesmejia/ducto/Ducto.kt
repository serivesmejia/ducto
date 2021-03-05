package com.github.serivesmejia.ducto

open class Ducto<I : Any, O : Any> {

    var parentScope: DuctoScope<O, I, out Any>? = null
        internal set

    internal var hasDeclaredFinally = false

    @Suppress("UNCHECKED_CAST")
    fun process(input: I) : O {
        validate()

        parentScope?.let {
            return it.execute(input) as O
        }

        //Unreachable, just so that the compiler doesn't complain
        throw IllegalStateException()
    }

    fun <o : Any> first(stage: Stage<I, o>): DuctoScope<O, I, o> {
        val scope = DuctoScope<O, I, o>(this)
        scope.stage = stage
        scope.part = DuctoScope.Part.FIRST

        parentScope = scope
        return scope
    }

    fun validate() {
        if(!hasDeclaredFinally) {
            throw IllegalStateException("Can't process or serialize before having a \"finally\" stage")
        }
        if(parentScope == null) {
            throw IllegalStateException("Can't process or serialize before having a \"first\" stage")
        }
    }

}