
# VariantMarketData

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **productId** | **kotlin.String** | Unique identifier for this product |  |
| **variantId** | **kotlin.String** | Unique identifier for this product variant |  |
| **currencyCode** | **kotlin.String** | The currency code. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  |
| **highestBidAmount** | **kotlin.String** | The highest bid for the product variant listed in the country requested. |  |
| **lowestAskAmount** | **kotlin.String** | The lowest ask for the product variant listed in the country requested. |  [optional] |
| **sellFasterAmount** | **kotlin.String** | The price you have to list at, inclusive of duties and taxes, to become the lowest Ask to buyers in the United States. |  [optional] |
| **earnMoreAmount** | **kotlin.String** | The price you have to list at, to become the lowest ask to buyers in your region. This accounts for VAT and taxes. |  [optional] |
| **flexLowestAskAmount** | **kotlin.String** | The Flex program&#39;s lowest ask for the product variant listed in the country requested. |  [optional] |



