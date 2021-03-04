package com.github.serivesmejia.ducto.serialization

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.Stage
import kotlin.reflect.KClass

object DuctoSerializer {

    fun Ducto<*, *>.toJson(): String {
        return Klaxon().toJsonString(this.data)
    }

    val Ducto<*, *>.data: DuctoData
        get() {
            val parentScopeData = DuctoScopeData(parentScope!!.stage!!::class.qualifiedName!!)
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