package com.github.serivesmejia.ducto.serialization

import com.github.serivesmejia.ducto.Stage

abstract class ParametizedStage<P : StageParameters, I : Any, O : Any>(internal var params: P) : Stage<I, O> {

    abstract fun process(input: I, params: P): O

    override fun process(input: I): O = process(input, params)

}