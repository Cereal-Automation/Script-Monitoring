package com.cereal.command.monitor.data.stockx

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.MarketItem
import com.cereal.command.monitor.models.MarketItemVariant
import com.cereal.command.monitor.models.SearchCriteria
import com.cereal.command.monitor.models.UnknownCurrencyException
import com.cereal.command.monitor.repository.MarketItemRepository
import com.cereal.stockx.api.CatalogApi
import com.cereal.stockx.api.model.CurrencyCode
import java.math.BigDecimal

/**
 * Repository implementation for retrieving market item data from StockX.
 * @param catalogApi The StockX API client.
 */
class StockXMarketItemRepository(private val catalogApi: CatalogApi) : MarketItemRepository {
    override suspend fun search(criteria: SearchCriteria): MarketItem? {
        val result = catalogApi.search(criteria.styleId, pageNumber = null, pageSize = 10)

        if (result.body().count == 0.0) {
            return null
        }

        return result.body().products.firstOrNull {
            it.styleId != null && it.styleId == criteria.styleId
        }?.let { stockxProduct ->
            val variants = catalogApi.getVariants(stockxProduct.productId)
            val itemUrl = "https://stockx.com/${stockxProduct.urlKey}"

            val marketItemVariants =
                variants.body().mapNotNull { stockxVariant ->
                    val marketData =
                        catalogApi.getVariantMarketData(
                            productId = stockxVariant.productId,
                            variantId = stockxVariant.variantId,
                            currencyCode = CurrencyCode.decode(criteria.currency.code),
                            country = null,
                        )

                    val highestBid = marketData.body().highestBidAmount ?: return@mapNotNull null
                    val highestBidAmountItem = BigDecimal(highestBid)
                    val currencyCode = marketData.body().currencyCode
                    val currency = Currency.fromCode(currencyCode) ?: throw UnknownCurrencyException(currencyCode)

                    MarketItemVariant(
                        id = stockxVariant.variantId,
                        properties =
                            listOf(
                                ItemProperty.Price(
                                    highestBidAmountItem,
                                    currency,
                                ),
                            ),
                    )
                }

            MarketItem(
                id = stockxProduct.productId,
                url = itemUrl,
                variants = marketItemVariants,
            )
        }
    }
}
