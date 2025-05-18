package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.MarketItemVariant
import com.cereal.command.monitor.models.SearchCriteria
import com.cereal.command.monitor.models.Variant
import com.cereal.command.monitor.models.getValue
import com.cereal.command.monitor.repository.MarketItemRepository
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Monitoring strategy that compares an item's price against marketplace prices.
 *
 * This strategy checks if the item's price is better (lower) than the market price,
 * and generates a notification if the price advantage exceeds a certain threshold.
 */
class MarketPriceComparisonStrategy(private val marketItemRepository: MarketItemRepository) : MonitorStrategy {
    companion object {
        // Absolute amount threshold to consider a price significantly better than market
        private val PRICE_ADVANTAGE_THRESHOLD = BigDecimal(10)
    }

    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        // Get all variants with styleIds to check against market prices
        val variantsWithStyleId = item.variants.filter { it.styleId != null }
        if (variantsWithStyleId.isEmpty()) return null

        val notifications = mutableListOf<String>()

        for (variant in variantsWithStyleId) {
            // Get the current variant's price
            val price = variant.getValue<ItemProperty.Price>() ?: item.getValue<ItemProperty.Price>() ?: continue

            // Create search criteria using the variant's styleId and the item's currency
            val searchCriteria = SearchCriteria(
                styleId = variant.styleId!!,
                currency = price.currency
            )

            // Search for market data
            val marketItem = marketItemRepository.search(searchCriteria) ?: continue

            // Find the matching variant in market data
            val matchingMarketVariant = findMatchingVariant(variant, marketItem.variants)
                ?: continue

            // Find the market price property
            val marketPrice = matchingMarketVariant.properties.filterIsInstance<ItemProperty.Price>().firstOrNull()
                ?: continue

            // If market price is higher than our item's price by the threshold amount or more
            if (isPriceSignificantlyBetter(price.value, marketPrice.value)) {
                val savingsAmount = marketPrice.value.subtract(price.value)
                val savingsPercentage = calculatePercentageDifference(price.value, marketPrice.value)

                val notification = buildString {
                    append("Price for ${item.name} ${variant.name} (${price}) is ")
                    append(formatSavingsPercentage(savingsPercentage))
                    append(" lower than market price (${marketPrice}). ")
                    append("You'll earn ${ItemProperty.Price(savingsAmount, price.currency)}")
                }
                notifications.add(notification)
            }
        }

        // Return all notifications joined together, or null if none
        return if (notifications.isNotEmpty()) notifications.joinToString("\n\n") else null
    }

    private fun isPriceSignificantlyBetter(itemPrice: BigDecimal, marketPrice: BigDecimal): Boolean {
        if (itemPrice >= marketPrice) return false

        val priceDifference = marketPrice.subtract(itemPrice)
        return priceDifference >= PRICE_ADVANTAGE_THRESHOLD
    }

    private fun calculatePercentageDifference(itemPrice: BigDecimal, marketPrice: BigDecimal): Double {
        return marketPrice.subtract(itemPrice)
            .divide(marketPrice, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
            .toDouble()
    }

    private fun formatSavingsPercentage(percentage: Double): String {
        return String.format("%.1f%%", percentage)
    }

    private fun findMatchingVariant(variant: Variant, marketVariants: List<MarketItemVariant>): MarketItemVariant? {
        val variantSize = variant.getValue<ItemProperty.Size>()
        if (variantSize != null) {
            marketVariants.firstOrNull { marketVariant ->
                val marketSize = marketVariant.properties.filterIsInstance<ItemProperty.Size>().firstOrNull()
                marketSize != null &&
                        marketSize.type == variantSize.type &&
                        marketSize.value == variantSize.value
            }?.let { return it }
        }

        return null
    }

    override fun requiresBaseline(): Boolean = false
}