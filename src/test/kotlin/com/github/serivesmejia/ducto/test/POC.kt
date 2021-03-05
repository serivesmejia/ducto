package com.github.serivesmejia.ducto.test

import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.serialization.ParametizedStage
import com.github.serivesmejia.ducto.RestrictedDucto
import com.github.serivesmejia.ducto.Stage
import com.github.serivesmejia.ducto.serialization.DuctoSerializer
import com.github.serivesmejia.ducto.serialization.DuctoSerializer.data
import com.github.serivesmejia.ducto.serialization.DuctoSerializer.toDucto
import com.github.serivesmejia.ducto.serialization.DuctoSerializer.toJson
import com.github.serivesmejia.ducto.serialization.StageParameters
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt

class POC {

    data class NumberParameters(val number: Double = 0.0) : StageParameters()

    class NumberMultiplyBy(params: NumberParameters) : ParametizedStage<NumberParameters, Double, Double>(params) {
        override fun process(input: Double, params: NumberParameters) = input * params.number
    }

    class NumberMultiplyByTwo : Stage<Double, Double> {
        override fun process(input: Double): Double = input * 2
    }

    class NumberDivideByTwenty : Stage<Double, Double> {
        override fun process(input: Double): Double = input / 20
    }

    @Test
    fun `Test Serialization POC`() {
        val ducto = Ducto<Double, Double>()

        ducto.first(NumberMultiplyBy(NumberParameters(5.0)))
            .then(NumberMultiplyByTwo())
            .finally(NumberDivideByTwenty())

        val data = ducto.data

        val json = ducto.toJson()
        println(json)

        val parsedData = DuctoSerializer.parseJsonToDuctoData(json)!!
        val parsedDucto = parsedData.toDucto<Double, Double>()

        assertEquals(data, parsedData)
        assertEquals(ducto.process(444.0), parsedDucto!!.process(444.0), 0.5)
    }

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

        assertEquals(ducto.process(52.8), "422.4, double")
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

        assertEquals(ducto.process(999.52), 1623.0, 0.5)
    }

}