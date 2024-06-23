package com.cereal.script.monitoring.domain.repository

import com.cereal.script.monitoring.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface ItemMonitorRepository {

    suspend fun getItems(): Flow<Item>

}
