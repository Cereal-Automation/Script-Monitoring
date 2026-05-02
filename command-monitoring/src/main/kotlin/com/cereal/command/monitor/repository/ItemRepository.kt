package com.cereal.command.monitor.repository

import com.cereal.command.monitor.models.Page

interface ItemRepository {
    val name: String

    suspend fun getItems(nextPageToken: String?): Page
}
