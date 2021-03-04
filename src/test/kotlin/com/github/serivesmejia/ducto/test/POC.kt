package com.github.serivesmejia.ducto.test

import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.RestrictedDucto
import org.junit.Test
import kotlin.math.roundToInt

class POC {

    @Test
    fun `Test Unrestricted POC`() {
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

    @Test
    fun `Test Restricted POC`() {
        val ducto = RestrictedDucto<Double, Double>()

        ducto.first {
            it * 94.2
        }.then {
            it / 58
        }.finally {
            it.roundToInt().toDouble()
        }

        println(ducto.process(999.52))
    }

}