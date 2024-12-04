package com.cereal.script.commands.monitor.domain

import com.cereal.script.commands.monitor.domain.models.Page

interface ItemRepository {
    suspend fun getItems(nextPageToken: String?): Page
}
