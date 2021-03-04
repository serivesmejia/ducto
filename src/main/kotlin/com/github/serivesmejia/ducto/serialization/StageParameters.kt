package com.github.serivesmejia.ducto.serialization

import com.beust.klaxon.TypeFor

@TypeFor(field = "type", adapter = DuctoSerializer.StageParametersAdapter::class)
open class StageParameters {
    var type = ""
        internal set

    internal fun defineType() {
        type = this::class.java.typeName
    }
}