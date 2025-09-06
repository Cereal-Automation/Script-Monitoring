package com.cereal.script.exception

/**
 * Marker interface for exceptions that should trigger a full restart of the command chain.
 * When an exception implementing this interface is thrown during command execution,
 * the entire command chain will be restarted from the beginning instead of just retrying
 * the current command.
 */
interface InvalidChainContextException
