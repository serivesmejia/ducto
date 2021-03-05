package com.github.serivesmejia.ducto

@Suppress("UNCHECKED_CAST")
class DuctoScope<R : Any, I : Any, O : Any>(private val parentDucto: Ducto<out Any, out Any>) {

    var stage: Stage<I, O>? = null

    var childScope: DuctoScope<R, out Any, out Any>? = null

    internal fun execute(input: Any): O {
        if(stage == null) {
            throw IllegalStateException("Stage is not defined for this scope")
        }

        var result: Any = stage!!.process(input as I)

        childScope?.let {
            result = it.execute(result)
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