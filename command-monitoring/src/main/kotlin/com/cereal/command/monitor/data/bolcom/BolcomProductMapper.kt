package com.cereal.command.monitor.data.bolcom

import com.cereal.command.monitor.data.bolcom.httpclient.model.BolProduct
import com.cereal.command.monitor.data.bolcom.httpclient.model.BolcomSearchResponse
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class BolcomProductMapper(
    private val baseUrl: String,
) {
    /**
     * Extracts products from the search response template.
     */
    fun extractProductsFromResponse(response: BolcomSearchResponse): List<BolProduct> {
        val template =
            response.customer
                ?.basket
                ?.totalQuantity
                ?.routesSearchPage
                ?.data
                ?.template
                ?: return emptyList()

        val products = mutableListOf<BolProduct>()

        for (region in template.regions) {
            for (slot in region.slots) {
                if (slot.type == "ProductItem") {
                    val product = slot.props?.product ?: continue
                    mapProductToBolProduct(product)?.let { products.add(it) }
                }
            }
        }

        return products
    }

    /**
     * Maps a BolcomSearchResponse.Product to BolProduct.
     */
    private fun mapProductToBolProduct(product: BolcomSearchResponse.Product): BolProduct? {
        val productId = product.id ?: return null

        val brand =
            product.relatedParties
                .firstOrNull { it.role == "BRAND" }
                ?.party?.name

        val imageUrl =
            product.assets
                .firstOrNull()
                ?.renditions?.firstOrNull()
                ?.url

        val description =
            product.attributes
                .firstOrNull { it.name == "Description" }
                ?.values?.firstOrNull()
                ?.value

        val bestOffer = product.bestSellingOffer
        val price = bestOffer?.sellingPrice?.price?.amount
        val discount = bestOffer?.sellingPriceDiscountOnStrikethroughPrice?.amount?.amount
        val regularPrice = bestOffer?.strikethroughPrice?.price?.amount
        val sellerName = bestOffer?.retailer?.name ?: bestOffer?.retailer?.id

        val orderable = determineOrderability(bestOffer)

        return BolProduct(
            productId = productId,
            title = product.title,
            slug = product.url,
            brand = brand,
            price = price,
            discount = discount?.toDoubleOrNull(),
            regularPrice = regularPrice,
            orderable = orderable,
            imageUrl = imageUrl,
            seller = sellerName,
            description = description,
        )
    }

    /**
     * Determines if a product is orderable based on delivery information.
     */
    private fun determineOrderability(bestOffer: BolcomSearchResponse.BestSellingOffer?): Boolean {
        val deliveryDesc = bestOffer?.bestDeliveryOption?.deliveryDescription
        return (
            deliveryDesc != null &&
                (
                    deliveryDesc.contains("Op voorraad", ignoreCase = true) ||
                        deliveryDesc.contains("Voor 23:00", ignoreCase = true) ||
                        deliveryDesc.contains("morgen in huis", ignoreCase = true)
                )
        ) || bestOffer?.deliveredWithin48Hours == true
    }

    /**
     * Maps a BolProduct to an Item.
     */
    fun mapBolProductToItem(productData: BolProduct): Item? {
        val title = productData.title ?: return null
        val properties = buildItemProperties(productData)

        return Item(
            id = productData.productId,
            url =
                buildString {
                    append(baseUrl)
                    append(productData.slug)
                },
            name = title,
            description = productData.description,
            imageUrl = productData.imageUrl,
            variants = emptyList(),
            properties = properties,
        )
    }

    /**
     * Builds the list of item properties from product data.
     */
    private fun buildItemProperties(productData: BolProduct): List<ItemProperty> {
        val properties = mutableListOf<ItemProperty>()

        properties.add(
            ItemProperty.Stock(
                isInStock = productData.orderable,
                amount = null,
                level = if (productData.orderable) ":white_check_mark:" else ":x:",
            ),
        )

        productData.seller?.let { seller ->
            properties.add(
                ItemProperty.Custom(
                    name = "Seller",
                    value = seller,
                ),
            )
        }

        productData.brand?.let { brand ->
            if (brand.isNotBlank()) {
                properties.add(ItemProperty.Custom(name = "Brand", value = brand))
            }
        }

        productData.price?.let { price ->
            properties.add(
                ItemProperty.Custom(
                    name = "Price",
                    value = formatPrice(price, productData.regularPrice),
                ),
            )
        }

        productData.discount?.let { discount ->
            properties.add(
                ItemProperty.Custom(
                    name = "Discount",
                    value = formatCurrency(discount),
                ),
            )
        }

        return properties
    }

    /**
     * Formats a price with optional strikethrough for regular price.
     */
    private fun formatPrice(
        price: Double,
        regularPrice: Double?,
    ): String =
        buildString {
            append(formatCurrency(price))
            regularPrice?.let {
                append(" ")
                append("~~${formatCurrency(it)}~~")
            }
        }

    /**
     * Formats a currency value.
     */
    private fun formatCurrency(amount: Double): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        currencyFormatter.currency = java.util.Currency.getInstance(Currency.EUR.code)
        return currencyFormatter.format(BigDecimal.valueOf(amount))
    }
}
