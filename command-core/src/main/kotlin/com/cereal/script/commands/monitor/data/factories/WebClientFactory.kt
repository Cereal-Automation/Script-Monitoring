package com.cereal.script.commands.monitor.data.factories

import com.cereal.sdk.models.proxy.Proxy
import org.htmlunit.DefaultCredentialsProvider
import org.htmlunit.ProxyConfig
import org.htmlunit.WebClient

object WebClientFactory {
    fun create(httpProxy: Proxy?): WebClient =
        WebClient().apply {
            options.isJavaScriptEnabled = false // Disable JavaScript for simplicity
            options.isCssEnabled = false // Disable CSS for faster loading

            httpProxy?.let { proxy ->
                options.proxyConfig = ProxyConfig(proxy.address, proxy.port, "http")
                // Configure proxy authentication
                val credentialsProvider = credentialsProvider as DefaultCredentialsProvider

                proxy.username?.let {
                    credentialsProvider.addCredentials(proxy.username, proxy.password?.toCharArray())
                }
            }
        }
}
