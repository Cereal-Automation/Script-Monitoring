package com.cereal.script.commands.monitor.data.snkrs

import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository

private const val PAGE_COUNT = 50

class SnkrsItemRepository(
    private val snkrsApiClient: SnkrsApiClient,
    private val locale: Locale,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?): Page {
        val anchor = nextPageToken?.toInt() ?: 0

        val result = snkrsApiClient.getProducts(locale, anchor, PAGE_COUNT)

        val newAnchor =
            if (result.size == PAGE_COUNT) {
                (anchor + PAGE_COUNT).toString()
            } else {
                null
            }

        return Page(newAnchor, result)
    }
}
