package com.cereal.command.monitor.data.bolcom

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * [ItemRepository] backed by bol.com's search endpoint.
 *
 * Queries every term in [searchTerms] concurrently and merges the results into a single page,
 * deduplicating by [Item.id] so a product that surfaces under multiple search terms is only
 * reported once (otherwise the monitor would notify once per duplicate).
 *
 * The search endpoint returns a single result page, so there is no pagination.
 */
class BolcomItemRepository(
    private val logRepository: LogRepository,
    private val searchTerms: List<String>,
    private val randomProxy: RandomProxy? = null,
) : ItemRepository {
    override val name: String = "bol.com"

    private val dataSource by lazy { BolcomWebDataSource(logRepository, randomProxy) }

    override suspend fun getItems(nextPageToken: String?): Page {
        val items =
            coroutineScope {
                searchTerms
                    .map { term -> async { dataSource.fetchItems(term) } }
                    .awaitAll()
                    .flatten()
                    .associateBy { it.id }
                    .values
                    .toList()
            }

        return Page(null, items)
    }
}
