package com.cereal.script.commands.monitor.data.snkrs

import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository

class SnkrsItemRepository(
    private val snkrsApiClient: SnkrsApiClient,
    private val locale: Locale,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?): Page {
        val anchor = nextPageToken?.toInt() ?: 0

        val result = snkrsApiClient.getProducts(locale, anchor, 50)
        return Page((anchor + 50).toString(), result)
    }
}
