package com.github.serivesmejia.ducto.serialization

import com.github.serivesmejia.ducto.Ducto
import com.github.serivesmejia.ducto.Stage
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.reflect.cast

object DuctoSerializer {

    fun Ducto<*, *>.toJson(): String {
        return Json { prettyPrint = true }.encodeToString(getDataFrom(this))
    }

    fun getDataFrom(ducto: Ducto<*, *>) : DuctoData {
        val parentScopeData = DuctoScopeData(ducto.parentScope!!.stage!!::class.qualifiedName!!)
        var currentScopeData = parentScopeData

        checkStageParameters(ducto.parentScope!!.stage!!, parentScopeData)

        var currentScope = ducto.parentScope!!

        while(currentScope.childScope != null) {
            val it = currentScope.childScope!!

            if(it.stage != null) {
                val scopeData = DuctoScopeData(it.stage!!::class.qualifiedName!!)

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

    private fun checkStageParameters(stage: Stage<out Any, out Any>, scopeData: DuctoScopeData) {
        if(stage is ParametizedStage<*, *, *>) {
            scopeData.stageParametersClassName = stage.params::class.qualifiedName
            scopeData.stageParameters = stage.params::class.cast(stage.params)
        }
    }

}