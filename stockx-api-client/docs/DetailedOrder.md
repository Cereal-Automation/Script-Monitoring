
# DetailedOrder

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **askId** | **kotlin.String** | Unique identifier for an ask on the StockX platform |  |
| **orderNumber** | **kotlin.String** | The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG |  |
| **listingId** | **kotlin.String** | Unique ID for this listing |  |
| **amount** | **kotlin.String** | The ask/order price |  |
| **currencyCode** | **kotlin.String** | The currency type for this order.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; |  |
| **createdAt** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | When the order was created in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z |  |
| **updatedAt** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | When the order was updated in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z |  |
| **variant** | [**Variant**](Variant.md) |  |  |
| **product** | [**OrderProduct**](OrderProduct.md) |  |  |
| **status** | [**ListOrdersStatus**](ListOrdersStatus.md) |  |  |
| **shipment** | [**Shipment**](Shipment.md) | The shipment details of the order. |  |
| **initiatedShipments** | [**ManifestDataResponse**](ManifestDataResponse.md) | An object containing details about the seller initiated shipments. |  |
| **inventoryType** | [**InventoryType**](InventoryType.md) |  |  |
| **payout** | [**Payout**](Payout.md) |  |  |
| **authenticationDetails** | [**AuthenticationDetails**](AuthenticationDetails.md) | Details about authentication status and failure notes |  [optional] |



