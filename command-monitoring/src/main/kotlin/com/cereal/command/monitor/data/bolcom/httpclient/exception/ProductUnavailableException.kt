package com.cereal.command.monitor.data.bolcom.httpclient.exception

import com.cereal.script.exception.InvalidChainContextException

class ProductUnavailableException(
    message: String? = null,
) : Exception(message),
    InvalidChainContextException
