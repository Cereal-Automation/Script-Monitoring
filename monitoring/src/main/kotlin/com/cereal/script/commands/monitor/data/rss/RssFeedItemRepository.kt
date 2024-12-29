package com.cereal.script.commands.monitor.data.rss

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.sdk.component.logger.LoggerComponent
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem
import java.text.SimpleDateFormat
import java.util.Locale

class RssFeedItemRepository(
    private val rssFeedUrl: String,
    private val logger: LoggerComponent,
    private val rssParser: RssParser = RssParser(),
) : ItemRepository {
    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

    override suspend fun getItems(nextPageToken: String?): Page {
        val rssChannel = rssParser.getRssChannel(rssFeedUrl)

        val items =
            rssChannel.items.mapNotNull {
                val id = it.guid
                val url = it.link
                val name = it.title

                if (id != null && url != null && name != null) {
                    val values =
                        listOfNotNull(
                            getPublishDate(it),
                        )
                    Item(id, url, name, description = it.description, imageUrl = it.image, properties = values)
                } else {
                    logger.warn("Skipping RSS feed item because empty values were found: [id=$id, url=$url, name=$name]")
                    null
                }
            }

        return Page(null, items)
    }

    private fun getPublishDate(rssItem: RssItem): ItemProperty.PublishDate? =
        try {
            ItemProperty.PublishDate(dateFormat.parse(rssItem.pubDate).toInstant())
        } catch (e: Exception) {
            logger.warn(
                "Expected to find a publish date for RSS item with guid ${rssItem.guid} but couldn't read the date because: ${e.message}",
            )
            null
        }
}
