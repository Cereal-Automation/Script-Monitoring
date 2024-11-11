package com.cereal.nike

import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.shared.BaseConfiguration

interface NikeConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_CATEGORY_URL,
        name = "Category URL",
        description = "The url of the category to monitor. For example https://www.nike.com/w/mens-shoes-nik1zy7ok",
    )
    fun categoryUrl(): String

    companion object {
        const val KEY_CATEGORY_URL = "category_url"
    }
}
