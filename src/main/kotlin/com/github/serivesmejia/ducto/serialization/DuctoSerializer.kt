package com.github.serivesmejia.ducto.serialization

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.DuctoScope
import com.github.serivesmejia.ducto.Stage
import com.github.serivesmejia.ducto.serialization.exception.IllegalDuctoDataException
import java.util.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object DuctoSerializer {

    fun Ducto<*, *>.toJson(): String {
        return Klaxon().toJsonString(this.data)
    }

    val Ducto<*, *>.data: DuctoData
        get() {
            validate()

            val parentScopeData = DuctoScopeData(parentScope!!.stage!!::class.java.typeName, parentScope!!.part)
            var currentScopeData = parentScopeData

            checkStageParameters(parentScope!!.stage!!, parentScopeData)

            var currentScope = parentScope!!

            while (currentScope.childScope != null) {
                val it = currentScope.childScope!!

                if (it.stage != null) {
                    val scopeData = DuctoScopeData(it.stage!!::class.java.typeName, it.part)

                    checkStageParameters(it.stage!!, scopeData)

                    currentScopeData.childScopeData = scopeData
                    currentScopeData = scopeData

                    currentScope = it
                } else {
                    break
                }
            }

            return DuctoData(parentScopeData)
        }

    fun parseJsonToDuctoData(json: String) = Klaxon().parse<DuctoData>(json)

    fun <I : Any, O : Any> parseJsonToDucto(json: String) = parseJsonToDuctoData(json)?.toDucto<I, O>()

    fun < I : Any,  O : Any> DuctoData.toDucto(): Ducto<I, O>? {

        val ducto = Ducto<I, O>()

        val firstScope = firstScopeData.toScope<O, Any, Any>(ducto)

        var currentScopeData = firstScopeData
        var currentScope = firstScope

        while(currentScopeData.childScopeData != null) {
            val childData = currentScopeData.childScopeData!!
            val childScope = childData.toScope<O, Any, Any>(ducto)

            if(childScope.part == DuctoScope.Part.FINALLY) {
                ducto.hasDeclaredFinally = true
            }

            currentScope.childScope = childScope
            currentScope = childScope
            currentScopeData = childData
        }

        ducto.parentScope = firstScope as DuctoScope<O, I, out Any>

        ducto.validate()

        return ducto
    }

    fun <R : Any, I : Any, O : Any> DuctoScopeData.toScope(parentDucto: Ducto<out Any, R>): DuctoScope<R, I, O> {
        val scope = DuctoScope<R, I, O>(parentDucto)
        val stageClass = Class.forName(stageClassName)

        if(stageParameters != null) {
            try {
                val constructor = stageClass.getConstructor(stageParameters!!::class.java)
                scope.stage = constructor.newInstance(stageParameters!!) as Stage<I, O>
            } catch(ex: NoSuchMethodException) {
                throw IllegalDuctoDataException(
                    "Stage class ${stageClass.name} should implement a constructor with a single parameter of type ${stageParameters!!::class.java.name}"
                )
            }
        } else {
            try {
                val constructor = stageClass.getConstructor()
                scope.stage = constructor.newInstance() as Stage<I, O>
            } catch(ex: NoSuchMethodException) {
                throw IllegalDuctoDataException(
                    "Stage class ${stageClass.name} should implement a constructor with no parameters"
                )
            }
        }

        scope.part = scopePart

        return scope
    }

    private fun checkStageParameters(stage: Stage<out Any, out Any>, scopeData: DuctoScopeData) {
        if (stage is ParametizedStage<*, *, *>) {
            stage.params.defineType()
            scopeData.stageParameters = stage.params
        }
    }

    class StageParametersAdapter : TypeAdapter<StageParameters> {
        override fun classFor(type: Any): KClass<out StageParameters> {
            val typeName = type as String
            return Class.forName(typeName).kotlin as KClass<out StageParameters>
        }
    }

}