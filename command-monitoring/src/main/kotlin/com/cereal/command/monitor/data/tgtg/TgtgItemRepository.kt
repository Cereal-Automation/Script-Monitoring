package com.cereal.command.monitor.data.tgtg

import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.command.monitor.data.tgtg.apiclients.models.FavoriteBusinessesRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.ItemDetails
import com.cereal.command.monitor.data.tgtg.apiclients.models.Store
import com.cereal.command.monitor.data.tgtg.apiclients.models.TgtgItem
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import java.math.BigDecimal

/**
 * Repository implementation for fetching TGTG (Too Good To Go) items using the TgtgApiClient.
 *
 * This repository converts TGTG business items into the standard Item format used by the monitoring system.
 * It fetches favorite businesses from a specified location and converts them to items with relevant properties
 * like price, stock availability, distance, and pickup information.
 */
class TgtgItemRepository(
    private val tgtgApiClient: TgtgApiClient,
    private val latitude: Double,
    private val longitude: Double,
    private val radius: Int = 50000,
    private val favoritesOnly: Boolean = false,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?): Page {
        // TGTG API doesn't support pagination in the traditional sense
        // The nextPageToken is ignored as the API returns all items in one call

        val favoriteBusinesses =
            tgtgApiClient.listFavoriteBusinesses(
                FavoriteBusinessesRequest(
                    favoritesOnly = favoritesOnly,
                    origin =
                        FavoriteBusinessesRequest.Origin(
                            latitude = latitude,
                            longitude = longitude,
                        ),
                    radius = radius,
                ),
            )

        val items =
            favoriteBusinesses?.items?.mapNotNull { tgtgItem ->
                convertTgtgItemToItem(tgtgItem)
            } ?: emptyList()

        // Since TGTG doesn't support pagination, nextPageToken is always null
        return Page(nextPageToken = null, items = items)
    }

    /**
     * Converts a TgtgItem from the API response to the standard Item format.
     */
    private fun convertTgtgItemToItem(tgtgItem: TgtgItem): Item? {
        val itemDetails = tgtgItem.item
        val store = tgtgItem.store

        // Skip items without essential information
        val itemId = itemDetails?.itemId ?: store?.storeId ?: return null
        val itemName = itemDetails?.name ?: tgtgItem.displayName ?: store?.storeName ?: "Unknown Item"

        // Build description from available information
        val description = buildDescription(tgtgItem, itemDetails, store)

        // Get image URL from item or store
        val imageUrl =
            itemDetails?.coverPicture?.currentUrl
                ?: itemDetails?.logoPicture?.currentUrl
                ?: store?.logoPicture?.currentUrl

        // Create properties list
        val properties = buildItemProperties(tgtgItem, itemDetails)

        return Item(
            id = itemId,
            // TGTG doesn't provide direct item URLs
            url = store?.website,
            name = itemName,
            description = description,
            imageUrl = imageUrl,
            variants = emptyList(),
            properties = properties,
        )
    }

    /**
     * Builds a comprehensive description from available TGTG item data.
     */
    private fun buildDescription(
        tgtgItem: TgtgItem,
        itemDetails: ItemDetails?,
        store: Store?,
    ): String {
        val parts = mutableListOf<String>()

        // Add item description
        itemDetails?.description?.let { if (it.isNotEmpty()) parts.add(it) }

        // Add store information
        store?.let { s ->
            s.storeName?.let { if (it.isNotEmpty()) parts.add("Store: $it") }
            s.branch?.let { if (it.isNotEmpty()) parts.add("Branch: $it") }
            s.description?.let { if (it.isNotEmpty()) parts.add("Store Description: $it") }
        }

        // Add food handling instructions
        itemDetails?.foodHandlingInstructions?.let {
            if (it.isNotEmpty()) parts.add("Food Handling: $it")
        }

        // Add collection info
        itemDetails?.collectionInfo?.let {
            if (it.isNotEmpty()) parts.add("Collection Info: $it")
        }

        // Add diet categories
        if (itemDetails?.dietCategories?.isNotEmpty() == true) {
            parts.add("Diet Categories: ${itemDetails.dietCategories.joinToString(", ")}")
        }

        // Add pickup interval
        tgtgItem.pickupInterval?.let { interval ->
            val start = interval.start ?: "Unknown"
            val end = interval.end ?: "Unknown"
            parts.add("Pickup Time: $start - $end")
        }

        return parts.joinToString("\n")
    }

    /**
     * Builds item-level properties from TGTG data.
     */
    private fun buildItemProperties(
        tgtgItem: TgtgItem,
        itemDetails: ItemDetails?,
    ): List<ItemProperty> {
        val properties = mutableListOf<ItemProperty>()

        // Add price if available
        itemDetails?.priceIncludingTaxes?.let { price ->
            val currency = Currency.fromCode(price.code ?: "EUR") ?: Currency.EUR
            val value = BigDecimal(price.minorUnits).divide(BigDecimal(100)) // Convert minor units to major units
            properties.add(ItemProperty.Price(value, currency))
        }

        // Add stock information
        val isInStock = tgtgItem.itemsAvailable > 0 && tgtgItem.inSalesWindow
        properties.add(
            ItemProperty.Stock(
                isInStock = isInStock,
                amount = if (tgtgItem.itemsAvailable > 0) tgtgItem.itemsAvailable else null,
                level =
                    when {
                        !tgtgItem.inSalesWindow -> "Not in sales window"
                        tgtgItem.itemsAvailable == 0 -> "Out of stock"
                        tgtgItem.itemsAvailable <= 3 -> "Low stock"
                        else -> "In stock"
                    },
            ),
        )

        // Add distance as custom property
        if (tgtgItem.distance > 0) {
            val distanceKm = String.format("%.2f", tgtgItem.distance / 1000.0)
            properties.add(ItemProperty.Custom("Distance", "${distanceKm}km"))
        }

        // Add favorite status
        if (tgtgItem.favorite) {
            properties.add(ItemProperty.Custom("Favorite", "Yes"))
        }

        // Add new item indicator
        if (tgtgItem.newItem) {
            properties.add(ItemProperty.Custom("New Item", "Yes"))
        }

        // Add item category
        itemDetails?.itemCategory?.let { category ->
            properties.add(ItemProperty.Custom("Category", category))
        }

        return properties
    }
}
