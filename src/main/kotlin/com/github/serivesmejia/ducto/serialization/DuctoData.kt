package com.github.serivesmejia.ducto.serialization

data class DuctoData(val firstScopeData: DuctoScopeData)

data class DuctoScopeData(val stageClassName: String,
                          var stageParameters: StageParameters? = null,
                          var childScopeData: DuctoScopeData? = null)