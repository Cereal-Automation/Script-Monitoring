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
    private var compareToPrice = price

    /**
     * Determines if the user should be notified about the item based on its price.
     *
     * @param item The item to be checked.
     * @return True if the item's price is equal to or below the specified price and in the correct currency, false otherwise.
     * @throws CurrencyMismatchException if the item's currency does not match the predefined currency.
     */
    override suspend fun shouldNotify(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean {
        val itemPrice = item.requireValue<ItemProperty.Price>()

        if (itemPrice.currency.code != currency.code) {
            throw CurrencyMismatchException(itemPrice.currency, currency)
        }

        val isCheaper = itemPrice.value <= compareToPrice

        if (isCheaper) {
            // Update the price so that we don't keep notifying the user when the next run also evaluates to the same
            // (lower) price.
            compareToPrice = itemPrice.value
        }

        return isCheaper
    }

    /**
     * Generates a notification message for an item based on its price.
     *
     * @param item The item for which the notification message is generated. It must contain a price.
     * @return A notification message in the format: "<item name> is available for <item price>".
     */
    override fun getNotificationMessage(item: Item): String {
        val itemPrice = item.requireValue<ItemProperty.Price>().value
        return "${item.name} is available for $itemPrice"
    }
}
