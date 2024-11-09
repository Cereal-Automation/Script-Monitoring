package com.cereal.script.monitoring.domain.models

sealed class DataSource {
    data class RssFeed(val rssFeedUrl: String) : DataSource()
}
