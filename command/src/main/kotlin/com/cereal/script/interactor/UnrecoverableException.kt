package com.cereal.script.interactor

/**
* Exception that indicates an unrecoverable error condition where retrying would not help.
* When this exception is thrown, retry mechanisms will be bypassed to avoid wasting resources.
*/
class UnrecoverableException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
