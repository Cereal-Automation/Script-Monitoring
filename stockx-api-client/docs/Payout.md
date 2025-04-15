
# Payout

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **totalPayout** | **kotlin.String** | The total amount to be paid for the sale of the product |  |
| **salePrice** | **kotlin.String** | The amount the product was sold for |  |
| **totalAdjustments** | **kotlin.String** | The sum of all adjustments made |  |
| **currencyCode** | **kotlin.String** | The currency code this product is being listed in. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  |
| **adjustments** | [**kotlin.collections.List&lt;Adjustment&gt;**](Adjustment.md) | The payout adjustment details if applicable |  |



