package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.data.item.nike.NikeApiItemRepository
import com.cereal.script.monitoring.domain.models.Item
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class TestNikeApiItemRepository {
    @Test
    fun testSuccess() =
        runBlocking {
            val repository = NikeApiItemRepository("https://www.nike.com/w/mens-shoes-nik1zy7ok")

            val collectedItems = mutableListOf<Item>()
            repository.getItems().collect { collectedItems.add(it) }
        }
}
