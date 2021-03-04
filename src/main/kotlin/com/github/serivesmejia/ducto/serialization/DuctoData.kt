package com.github.serivesmejia.ducto.serialization

import kotlinx.serialization.Serializable

@Serializable
data class DuctoData(val firstScopeData: DuctoScopeData)

@Serializable
data class DuctoScopeData(val stageClassName: String,
                          var stageParameters: StageParameters? = null,
                          var stageParametersClassName: String? = null,
                          var childScopeData: DuctoScopeData? = null)