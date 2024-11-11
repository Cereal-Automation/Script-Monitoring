package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.CurrencyMismatchException
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.requireValue
import java.math.BigDecimal

class EqualsOrBelowPriceMonitorStrategy(
    private val price: BigDecimal,
    private val currency: Currency,
) : MonitorStrategy {
    override suspend fun shouldNotify(item: Item): Boolean {
        val itemPrice = item.requireValue<ItemValue.Price>()

        if (itemPrice.currency.code != currency.code) {
            throw CurrencyMismatchException(itemPrice.currency, currency)
        }

        return itemPrice.value <= price
    }

    override fun getNotificationMessage(item: Item): String {
        val itemPrice = item.requireValue<ItemValue.Price>().value
        return "${item.name} is available for $itemPrice"
    }
}