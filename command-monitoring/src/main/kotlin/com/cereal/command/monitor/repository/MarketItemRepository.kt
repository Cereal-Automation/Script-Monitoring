package com.cereal.command.monitor.repository

import com.cereal.command.monitor.models.MarketItem
import com.cereal.command.monitor.models.SearchCriteria

interface MarketItemRepository {
    /**
     * Searches for an item based on the provided search criteria.
     *
     * @param criteria The criteria used to search for the item, including properties such as SKU.
     * @return The matching item if found, or null if no item fulfills the criteria.
     */
    suspend fun search(criteria: SearchCriteria): MarketItem?
}
