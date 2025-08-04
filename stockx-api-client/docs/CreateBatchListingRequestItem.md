
# CreateBatchListingRequestItem

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **quantity** | **kotlin.Double** | The total number of Listings that need to be created. |  |
| **variantId** | **kotlin.String** | Unique StockX variant ID that this Listing is being created for |  |
| **amount** | **kotlin.String** | The amount this Listing is being listed for |  |
| **active** | **kotlin.Boolean** | Flag used to indicate that the listing should be active or not |  [optional] |
| **currencyCode** | **kotlin.String** | The currency this Listing is being listed in. If not provided, it will default to USD.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  [optional] |
| **expiresAt** | **kotlin.String** | UTC timestamp representing when this Listing should auto-expire.  If not provided, it will default to 365 days from today. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  [optional] |



