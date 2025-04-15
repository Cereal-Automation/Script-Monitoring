
# CreateListingInput

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.String** | The amount this product is being listed for |  |
| **variantId** | **kotlin.String** | Unique StockX variant ID that this listing is being created for |  |
| **currencyCode** | **kotlin.String** | The currency code this product is being listed in. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  [optional] |
| **expiresAt** | **kotlin.String** | UTC timestamp representing when this Ask should auto-expire.  If not provided, it will default to 365 days from today.  Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  [optional] |
| **active** | **kotlin.Boolean** | A flag that defaults to true, activating the listing on the StockX marketplace |  [optional] |



