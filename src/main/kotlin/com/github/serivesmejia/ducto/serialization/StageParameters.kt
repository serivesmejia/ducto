package com.github.serivesmejia.ducto.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Parameters

open class StageParameters : Parameters()