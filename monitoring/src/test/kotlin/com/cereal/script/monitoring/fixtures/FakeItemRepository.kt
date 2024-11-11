package com.cereal.script.monitoring.fixtures

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeItemRepository(
    private val items: List<Item>,
) : ItemRepository {
    override suspend fun getItems(): Flow<Item> =
        flow {
            items.forEach { item -> emit(item) }
        }
}
