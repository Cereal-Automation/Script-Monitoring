
# ListingResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **listingId** | **kotlin.String** | Unique ID for this listing |  |
| **status** | **kotlin.String** | The current status of the listing&lt;br&gt;&lt;br&gt;Available values: \&quot;INACTIVE\&quot;, \&quot;ACTIVE\&quot;, \&quot;DELETED\&quot;, \&quot;CANCELED\&quot;, \&quot;MATCHED\&quot;, \&quot;COMPLETED\&quot; |  |
| **amount** | **kotlin.String** | The amount this product is being listed for |  |
| **currencyCode** | **kotlin.String** | The currency code. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  |
| **inventoryType** | **kotlin.String** | A representation of the type of inventory being listed |  |
| **createdAt** | **kotlin.String** | When the listing was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **updatedAt** | **kotlin.String** | When this listing was last updated in UTC.  Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **batch** | [**ListingsResponseBatch**](ListingsResponseBatch.md) | Information about the batch this listing is part of, if this listing was touched with the batch APIs |  |
| **ask** | [**ListingResponseAsk**](ListingResponseAsk.md) | An object with ask details |  |
| **order** | [**ListingResponseOrder**](ListingResponseOrder.md) | An object containing the order details |  |
| **product** | [**ListingResponseProduct**](ListingResponseProduct.md) | An object containing the product details. |  |
| **variant** | [**ListingResponseVariant**](ListingResponseVariant.md) | Details about the particular product variant |  |
| **authenticationDetails** | [**AuthenticationDetails**](AuthenticationDetails.md) | Details about authentication status and failure notes |  |
| **payout** | [**Payout**](Payout.md) | The payout object that contains the payout details and any adjustments like selling fees, shipping fees, taxes, etc. |  |
| **lastOperation** | [**ListingResponseOperation**](ListingResponseOperation.md) | An object containing the the details of the operation. |  |
| **initiatedShipments** | [**ManifestDataResponse**](ManifestDataResponse.md) | Details about manifests associated with the listing |  |



