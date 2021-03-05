package com.github.serivesmejia.ducto.serialization

import com.github.serivesmejia.ducto.DuctoScope

data class DuctoData(val firstScopeData: DuctoScopeData)

data class DuctoScopeData(val stageClassName: String,
                          val scopePart: DuctoScope.Part,
                          var stageParameters: StageParameters? = null,
                          var childScopeData: DuctoScopeData? = null)