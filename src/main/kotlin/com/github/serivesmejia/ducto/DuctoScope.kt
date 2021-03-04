package com.github.serivesmejia.ducto

class DuctoScope<R : Any, I : Any, O : Any>(private val parentDucto: Ducto<out Any, out Any>,
                                            internal var stage: Stage<I, O>? = null) {

    private var childScope: DuctoScope<R, out Any, out Any>? = null

    internal fun internalExecute(input: Any): O {
        return execute(input as I)
    }

    private fun execute(input: I): O {
        if(stage == null) {
            throw IllegalStateException("Stage is not defined for this scope")
        }

        var result: Any = stage!!.process(input)

        childScope?.let {
            result = it.internalExecute(result)
        }

        return result as O
    }

    fun <o : Any> then(stage: Stage<I, o>): DuctoScope<R, I, o> {
        val scope = DuctoScope<R, I, o>(parentDucto)
        scope.stage = stage

        childScope = scope
        return scope
    }

    fun finally(stage: Stage<O, R>) {
        val scope = DuctoScope<R, O, R>(parentDucto)
        scope.stage = stage

        parentDucto.hasDeclaredFinally = true

        childScope = scope
    }

}