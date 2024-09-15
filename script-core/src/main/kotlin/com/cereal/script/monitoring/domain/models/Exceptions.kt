package com.cereal.script.monitoring.domain.models

import kotlin.reflect.KClass

class MissingValueTypeException(private val valueCls: KClass<*>): Exception("Items should contain a value of type ${valueCls::simpleName}.")
