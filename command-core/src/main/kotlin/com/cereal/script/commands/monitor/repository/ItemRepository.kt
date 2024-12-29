package com.cereal.script.commands.monitor.repository

import com.cereal.script.commands.monitor.models.Page

interface ItemRepository {
    suspend fun getItems(nextPageToken: String?): Page
}
