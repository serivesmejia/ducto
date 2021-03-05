package com.github.serivesmejia.ducto.serialization

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.DuctoScope
import com.github.serivesmejia.ducto.Stage
import kotlin.reflect.KClass

object DuctoSerializer {

    fun Ducto<*, *>.toJson(): String {
        return Klaxon().toJsonString(this.data)
    }

    val Ducto<*, *>.data: DuctoData
        get() {
            validate()

            val parentScopeData = DuctoScopeData(parentScope!!.stage!!::class.java.typeName)
            var currentScopeData = parentScopeData

            checkStageParameters(parentScope!!.stage!!, parentScopeData)

            var currentScope = parentScope!!

            while (currentScope.childScope != null) {
                val it = currentScope.childScope!!

                if (it.stage != null) {
                    val scopeData = DuctoScopeData(it.stage!!::class.java.typeName)

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

    inline fun <reified I : Any, reified O : Any> DuctoData.toDucto(): Ducto<I, O> {

        val ducto = Ducto<I, O>()

        val firstScope = firstScopeData.toScope<O, Any, Any>(ducto)

        var currentScopeData = firstScopeData
        var currentScope = firstScope

        while(currentScopeData.childScopeData != null) {
            val childData = currentScopeData.childScopeData!!
            val childScope = childData.toScope<O, Any, Any>(ducto)

            currentScope.childScope = childScope
            currentScope = childScope
            currentScopeData = childData
        }

        ducto.parentScope = firstScope as DuctoScope<O, I, out Any>

        return ducto
    }

    inline fun <reified R : Any, reified I : Any, reified O : Any> DuctoScopeData.toScope(parentDucto: Ducto<out Any, R>): DuctoScope<R, I, O> {
        val scope = DuctoScope<R, I, O>(parentDucto)
        val stageClass = Class.forName(stageClassName)

        if(stageParameters != null) {
            val constructor = stageClass.getConstructor(stageParameters!!::class.java)
            scope.stage = constructor.newInstance(stageParameters!!) as Stage<I, O>
        } else {
            val constructor = stageClass.getConstructor()
            scope.stage = constructor.newInstance() as Stage<I, O>
        }

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