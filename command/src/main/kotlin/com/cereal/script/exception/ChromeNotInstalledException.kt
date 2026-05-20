package com.cereal.script.exception

/**
 * Thrown when a browser-backed command cannot find a Chrome installation on the
 * current machine. Wraps the underlying browser-automation library's exception so
 * higher-level code can render a user-friendly error without depending on that
 * library directly.
 */
class ChromeNotInstalledException(
    cause: Throwable? = null,
) : Exception(
        "Google Chrome is not installed or could not be found. " +
            "Please install Google Chrome and try again.",
        cause,
    )
