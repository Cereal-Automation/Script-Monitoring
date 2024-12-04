package com.cereal.script.commands.monitor.fixtures

import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.Page

class FakeItemRepository(
    private val items: List<Item>,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?) = Page(null, items)
}
