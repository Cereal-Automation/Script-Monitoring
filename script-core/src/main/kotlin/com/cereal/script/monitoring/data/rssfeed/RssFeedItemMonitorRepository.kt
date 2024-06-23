package com.cereal.script.monitoring.data.rssfeed

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.ItemMonitorRepository
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RssFeedItemMonitorRepository(private val rssFeedUrl: String): ItemMonitorRepository {

    private val rssParser = RssParser()

    override suspend fun getItems(): Flow<Item> {
        return flow {
            val rssChannel = rssParser.getRssChannel(rssFeedUrl)

            rssChannel.items.forEach {
                val id = it.guid
                val url = it.sourceUrl
                val name = it.title

                if(id != null && url != null && name != null) {
                    val item = Item(id, url, name)
                    emit(item)
                }
            }
        }
    }
}
