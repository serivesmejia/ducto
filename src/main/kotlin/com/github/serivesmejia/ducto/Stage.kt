package com.github.serivesmejia.ducto

fun interface Stage<I : Any, O : Any>  {
    fun process(input: I) : O
}