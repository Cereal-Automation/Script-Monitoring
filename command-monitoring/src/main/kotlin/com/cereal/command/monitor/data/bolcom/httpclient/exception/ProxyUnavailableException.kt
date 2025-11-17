package com.cereal.command.monitor.data.bolcom.httpclient.exception

import com.cereal.script.exception.InvalidChainContextException

class ProxyUnavailableException(
    message: String? = null,
) : Exception(message),
    InvalidChainContextException
