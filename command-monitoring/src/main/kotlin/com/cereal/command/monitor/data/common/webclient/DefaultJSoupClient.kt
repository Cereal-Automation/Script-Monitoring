package com.cereal.command.monitor.data.common.webclient

import com.cereal.command.monitor.data.common.useragent.DESKTOP_USER_AGENTS
import com.cereal.sdk.models.proxy.Proxy
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.InetSocketAddress
import kotlin.time.Duration

fun defaultJSoupClient(
    url: String,
    timeout: Duration,
    httpProxy: Proxy?,
): Connection {
    val proxy =
        httpProxy?.let {
            java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress(it.address, it.port))
        }

    return Jsoup
        .connect(url)
        .timeout(timeout.inWholeMilliseconds.toInt())
        .proxy(proxy)
        .ignoreContentType(true) // Prevents errors from non-HTML content
        .ignoreHttpErrors(true) // Avoids stopping on HTTP errors
        .followRedirects(true)
        .userAgent(DESKTOP_USER_AGENTS.random())
}
