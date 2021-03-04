package com.github.serivesmejia.ducto.test

import com.github.serivesmejia.ducto.Ducto
import org.junit.Test

class POC {

    @Test
    fun poc() {
        val ducto = Ducto<Double, String>()

        ducto.first {
            it * 4
        }.then {
            it * 2
        }.then {
            it.toString()
        }.finally {
            "$it, double"
        }

        println(ducto.process(52.8))
    }

}