package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.CurrencyMismatchException
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.requireValue
import java.math.BigDecimal

/**
 * A strategy that monitors if the price of an item is equal to or below a specified target price.
 *
 * @property price The target price to compare against.
 * @property currency The currency in which the target price is specified.
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
