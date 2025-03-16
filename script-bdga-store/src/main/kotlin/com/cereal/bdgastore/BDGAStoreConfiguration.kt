package com.cereal.bdgastore

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy

interface BDGAStoreConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_RANDOM_PROXY,
        name = "Proxies",
        description =
            "The proxy to use when scraping the BDGA store. If multiple proxies are available, they will" +
                "be rotated at each request.",
    )
    fun proxy(): RandomProxy?

    companion object {
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
