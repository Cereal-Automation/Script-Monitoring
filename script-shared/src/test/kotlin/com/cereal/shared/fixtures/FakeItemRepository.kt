package com.cereal.shared.fixtures

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository

class FakeItemRepository(
    private val items: List<Item>,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?) = Page(null, items)
}
