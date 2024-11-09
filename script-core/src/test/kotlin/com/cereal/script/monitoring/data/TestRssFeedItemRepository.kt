package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.data.item.RssFeedItemRepository
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.sdk.component.logger.LoggerComponent
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Instant

class TestRssFeedItemRepository {
    @Test
    fun testParseRss() =
        runBlocking {
            val rssParser = mockk<RssParser>()
            coEvery { rssParser.getRssChannel(any()) } returns buildRssChannel()
            val repository = RssFeedItemRepository("http://foo.bar", mockk<LoggerComponent>(), rssParser)

            val collectedItems = mutableListOf<Item>()
            repository.getItems().collect { collectedItems.add(it) }

            assertEquals(
                listOf(
                    Item(
                        "foo",
                        url = "http://bar.baz",
                        name = "bar",
                        values =
                            listOf(
                                ItemValue.PublishDate(
                                    Instant.ofEpochSecond(1726558620L),
                                ),
                            ),
                    ),
                ),
                collectedItems,
            )
        }

    private fun buildRssChannel(): RssChannel {
        return RssChannel(
            items =
                listOf(
                    RssItem(
                        guid = "foo",
                        title = "bar",
                        author = null,
                        link = "http://bar.baz",
                        pubDate = "Tue, 17 Sep 2024 07:37:00 GMT",
                        description = null,
                        content = null,
                        image = null,
                        audio = null,
                        video = null,
                        sourceName = null,
                        sourceUrl = null,
                        categories = listOf(),
                        itunesItemData = null,
                        commentsUrl = null,
                    ),
                ),
            title = null,
            link = null,
            description = null,
            image = null,
            lastBuildDate = null,
            updatePeriod = null,
            itunesChannelData = null,
        )
    }
}
