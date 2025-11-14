package com.cereal.command.monitor.data.rss

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.sdk.component.logger.LoggerComponent
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TestRssFeedItemRepository {
    @Test
    fun testParseRss() =
        runBlocking {
            val rssParser = mockk<RssParser>()
            coEvery { rssParser.getRssChannel(any()) } returns buildRssChannel()
            val repository = RssFeedItemRepository("http://foo.bar", mockk<LoggerComponent>(), rssParser)

            val result = repository.getItems(null)

            assertEquals(
                Page(
                    items =
                        listOf(
                            Item(
                                "foo",
                                url = "http://bar.baz",
                                name = "bar",
                                properties =
                                    listOf(
                                        ItemProperty.PublishDate(
                                            Instant.fromEpochSeconds(1726558620L),
                                        ),
                                    ),
                            ),
                        ),
                    nextPageToken = null,
                ),
                result,
            )
        }

    private fun buildRssChannel(): RssChannel =
        RssChannel(
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
                        youtubeItemData = null,
                        rawEnclosure = null,
                    ),
                ),
            title = null,
            link = null,
            description = null,
            image = null,
            lastBuildDate = null,
            updatePeriod = null,
            itunesChannelData = null,
            youtubeChannelData = null,
        )
}
