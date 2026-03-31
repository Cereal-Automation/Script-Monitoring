package com.cereal.script.interactor

/**
 * Exception that indicates a command has finished successfully but shouldn't continue its own execution (e.g., skip).
 * When this exception is thrown, it's treated as a successful completion of the command.
 */
class CommandSuccessException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
