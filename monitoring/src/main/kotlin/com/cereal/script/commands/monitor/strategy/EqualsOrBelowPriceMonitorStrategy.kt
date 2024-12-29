package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Currency
import com.cereal.script.commands.monitor.models.CurrencyMismatchException
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.requireValue
import java.math.BigDecimal

/**
 * Monitoring strategy that notifies when the price of an item is equal to or below a specified threshold.
 *
 * This strategy implements the [MonitorStrategy] interface to track items whose price:
 * - Is equal to or below a predefined value in the specified currency.
 * - Differs from the price in its previous state (to avoid duplicate notifications).
 *
 * If the item's currency does not match the predefined currency, the method throws a [CurrencyMismatchException].
 *
 * Provides a [requiresBaseline] method to indicate whether a baseline (previously observed item state) is required
 * for the strategy to function. In this implementation, no baseline is required as the notification is based on
 * the item's current price relative to the specified threshold.
 */
class EqualsOrBelowPriceMonitorStrategy(
    private val price: BigDecimal,
    private val currency: Currency,
) : MonitorStrategy {
    /**
     * Determines if the user should be notified about the item based on its price.
     *
     * @param item The item to be checked.
     * @return True if the item's price is equal to or below the specified price and in the correct currency, false otherwise.
     * @throws CurrencyMismatchException if the item's currency does not match the predefined currency.
     */
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        val itemPrice = item.requireValue<ItemProperty.Price>()
        val previousItemPrice = previousItem?.requireValue<ItemProperty.Price>()

        // Do not notify when the price is the same as before.
        if (itemPrice.value == previousItemPrice?.value) return null

        if (itemPrice.currency.code != currency.code) {
            throw CurrencyMismatchException(itemPrice.currency, currency)
        }

        val isCheaper = itemPrice.value <= price.min(previousItemPrice?.value ?: price)

        return if (isCheaper) {
            "${item.name} is available for $itemPrice"
        } else {
            null
        }
    }

    override fun requiresBaseline(): Boolean = false
}
