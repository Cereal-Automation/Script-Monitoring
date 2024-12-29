package com.cereal.script.commands.monitor.fixtures

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository

class FakeItemRepository(
    private val items: List<Item>,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?) = Page(null, items)
}
