package com.cereal.command.monitor.data.rss

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.sdk.component.logger.LoggerComponent
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

class RssFeedItemRepository(
    private val rssFeedUrl: String,
    private val logger: LoggerComponent,
    private val rssParser: RssParser = RssParser(),
) : ItemRepository {
    override val name: String = rssFeedUrl

    companion object {
        const val PROPERTY_AUTHOR = "author"
        const val PROPERTY_CATEGORY = "category"
    }

    private val dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME

    override suspend fun getItems(nextPageToken: String?): Page {
        val rssChannel = rssParser.getRssChannel(rssFeedUrl)

        val items =
            rssChannel.items.mapNotNull { rssItem ->
                val id = rssItem.guid
                val url = rssItem.link
                val name = rssItem.title

                if (id != null && url != null && name != null) {
                    val values =
                        buildList<ItemProperty> {
                            getPublishDate(rssItem)?.let { date -> add(date) }

                            rssItem.author?.let { author ->
                                if (author.isNotBlank()) {
                                    add(ItemProperty.Custom(PROPERTY_AUTHOR, author))
                                }
                            }

                            rssItem.categories.forEach { category ->
                                if (category.isNotBlank()) {
                                    add(ItemProperty.Custom(PROPERTY_CATEGORY, category))
                                }
                            }
                        }

                    Item(id, url, name, description = rssItem.description, imageUrl = rssItem.image, properties = values)
                } else {
                    logger.warn("Skipping RSS feed item because empty values were found: [id=$id, url=$url, name=$name]")
                    null
                }
            }

        return Page(null, items)
    }

    @OptIn(ExperimentalTime::class)
    private fun getPublishDate(rssItem: RssItem): ItemProperty.PublishDate? {
        val pubDate = rssItem.pubDate ?: return null
        return try {
            ItemProperty.PublishDate(ZonedDateTime.parse(pubDate, dateTimeFormatter).toInstant().toKotlinInstant())
        } catch (e: Exception) {
            logger.error(
                "Expected to find a publish date for RSS item with guid ${rssItem.guid} but couldn't read the date because: ${e.message}",
                e,
            )
            null
        }
    }
}
