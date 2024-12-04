package com.cereal.script.commands.monitor.domain.models

import kotlin.reflect.KClass

class MissingValueTypeException(
    val valueCls: KClass<*>,
) : Exception("Item is missing a value of type ${valueCls::simpleName}.")

class CurrencyMismatchException(
    itemCurrency: Currency,
    requestedCurrency: Currency,
) : Exception("The currency of an item ${itemCurrency.code} doesn't match the requested currency ${requestedCurrency.code}.")
