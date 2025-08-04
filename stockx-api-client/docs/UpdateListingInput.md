
# UpdateListingInput

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.String** | The amount that will appear on stockx.com The amount this product is being listed for |  [optional] |
| **currencyCode** | **kotlin.String** | Currency in which the ask is placed&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; The currency code this product is being listed in. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  [optional] |
| **expiresAt** | **kotlin.String** | When the ask expires UTC timestamp representing when this listing should auto-expire.  If not provided, it will default to 365 days from today. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  [optional] |



