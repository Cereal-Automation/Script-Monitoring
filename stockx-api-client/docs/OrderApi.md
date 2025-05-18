# OrderApi

All URIs are relative to *https://api.stockx.com/v2*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getHistoricalOrders**](OrderApi.md#getHistoricalOrders) | **GET** /selling/orders/history | Get historical orders |
| [**getOrder**](OrderApi.md#getOrder) | **GET** /selling/orders/{orderNumber} | Get single order |
| [**getOrderShipment**](OrderApi.md#getOrderShipment) | **GET** /selling/orders/{orderNumber}/shipping-document/{shippingId} | Get shipping document |
| [**getOrders**](OrderApi.md#getOrders) | **GET** /selling/orders/active | Get active orders |


<a id="getHistoricalOrders"></a>
# **getHistoricalOrders**
> Orders getHistoricalOrders(fromDate, toDate, pageNumber, pageSize, orderStatus, productId, variantId, inventoryTypes, initiatedShipmentDisplayIds)

Get historical orders

Get all historical orders. Multiple filters are available.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = OrderApi()
val fromDate : kotlin.String = fromDate_example // kotlin.String | The start date of when the orders were created. Format is YYYY-MM-DD.
val toDate : kotlin.String = toDate_example // kotlin.String | The end date of when the orders were created. Format is YYYY-MM-DD.
val pageNumber : kotlin.Int = 1 // kotlin.Int | The requested page number. By default the page number is 1
val pageSize : kotlin.Int = 100 // kotlin.Int | The number of orders to be returned. By default the number is 10
val orderStatus : kotlin.String = CANCELED // kotlin.String | To filter your Orders by a given order status<br><br>Available values: \"AUTHFAILED\", \"DIDNOTSHIP\", \"CANCELED\", \"COMPLETED\", \"RETURNED\"
val productId : kotlin.String = productId_example // kotlin.String | Unique StockX product ID
val variantId : kotlin.String = variantId_example // kotlin.String | Unique StockX variant ID
val inventoryTypes : kotlin.String = STANDARD // kotlin.String | Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\"| ''). The inventory types are STANDARD or FLEX.
val initiatedShipmentDisplayIds : kotlin.String = initiatedShipmentDisplayIds_example // kotlin.String | The shipment's unique display id associated with the order. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro.
try {
    val result : Orders = apiInstance.getHistoricalOrders(fromDate, toDate, pageNumber, pageSize, orderStatus, productId, variantId, inventoryTypes, initiatedShipmentDisplayIds)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderApi#getHistoricalOrders")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderApi#getHistoricalOrders")
    e.printStackTrace()
}
```

### Parameters
| **fromDate** | **kotlin.String**| The start date of when the orders were created. Format is YYYY-MM-DD. | [optional] |
| **toDate** | **kotlin.String**| The end date of when the orders were created. Format is YYYY-MM-DD. | [optional] |
| **pageNumber** | **kotlin.Int**| The requested page number. By default the page number is 1 | [optional] |
| **pageSize** | **kotlin.Int**| The number of orders to be returned. By default the number is 10 | [optional] |
| **orderStatus** | **kotlin.String**| To filter your Orders by a given order status&lt;br&gt;&lt;br&gt;Available values: \&quot;AUTHFAILED\&quot;, \&quot;DIDNOTSHIP\&quot;, \&quot;CANCELED\&quot;, \&quot;COMPLETED\&quot;, \&quot;RETURNED\&quot; | [optional] |
| **productId** | **kotlin.String**| Unique StockX product ID | [optional] |
| **variantId** | **kotlin.String**| Unique StockX variant ID | [optional] |
| **inventoryTypes** | **kotlin.String**| Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\&quot;| &#39;&#39;). The inventory types are STANDARD or FLEX. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **initiatedShipmentDisplayIds** | **kotlin.String**| The shipment&#39;s unique display id associated with the order. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro. | [optional] |

### Return type

[**Orders**](Orders.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getOrder"></a>
# **getOrder**
> DetailedOrder getOrder(orderNumber)

Get single order

Get order details API allows you to fetch details for a single order by order number. This includes shipping and payout information.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = OrderApi()
val orderNumber : kotlin.String = orderNumber_example // kotlin.String | The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG
try {
    val result : DetailedOrder = apiInstance.getOrder(orderNumber)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderApi#getOrder")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderApi#getOrder")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **orderNumber** | **kotlin.String**| The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG | |

### Return type

[**DetailedOrder**](DetailedOrder.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getOrderShipment"></a>
# **getOrderShipment**
> kotlin.Any getOrderShipment(jwtAuthorization, authorization, orderNumber, shippingId)

Get shipping document

Get an existing shipping document.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = OrderApi()
val jwtAuthorization : kotlin.String = jwtAuthorization_example // kotlin.String | 
val authorization : kotlin.String = authorization_example // kotlin.String | 
val orderNumber : kotlin.String = 323314425-323214184 // kotlin.String | The number of order to fetch
val shippingId : kotlin.String = 323314425-323214184 // kotlin.String | The shipping ID of order to fetch
try {
    val result : kotlin.Any = apiInstance.getOrderShipment(jwtAuthorization, authorization, orderNumber, shippingId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderApi#getOrderShipment")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderApi#getOrderShipment")
    e.printStackTrace()
}
```

### Parameters
| **jwtAuthorization** | **kotlin.String**|  | |
| **authorization** | **kotlin.String**|  | |
| **orderNumber** | **kotlin.String**| The number of order to fetch | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **shippingId** | **kotlin.String**| The shipping ID of order to fetch | |

### Return type

[**kotlin.Any**](kotlin.Any.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf, application/json

<a id="getOrders"></a>
# **getOrders**
> Orders getOrders(pageNumber, pageSize, orderStatus, productId, variantId, sortOrder, inventoryTypes, initiatedShipmentDisplayIds)

Get active orders

Get all active orders API allows you to view all their active orders in the StockX marketplace. An order is considered active from the time it was created to the time the product was received and authenticated by StockX and the seller is paid out.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = OrderApi()
val pageNumber : kotlin.Int = 56 // kotlin.Int | The number of page
val pageSize : kotlin.Int = 56 // kotlin.Int | Requested page number. Starts at 1
val orderStatus : kotlin.String = CREATED // kotlin.String | To filter your Orders by a given order status<br><br>Available values: \"CREATED\", \"CCAUTHORIZATIONFAILED\", \"SHIPPED\", \"RECEIVED\", \"AUTHENTICATING\", \"AUTHENTICATED\", \"PAYOUTPENDING\", \"PAYOUTCOMPLETED\", \"SYSTEMFULFILLED\", \"PAYOUTFAILED\", \"SUSPENDED\"
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
val variantId : kotlin.String = variantId_example // kotlin.String | Unique identifier for a products variant
val sortOrder : kotlin.String = sortOrder_example // kotlin.String | The field by which the results are sorted. Defaults to \"CREATEDAT\" and can also accept \"SHIPBYDATE\".
val inventoryTypes : kotlin.String = STANDARD // kotlin.String | Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\"| ''). The inventory types are STANDARD or FLEX.
val initiatedShipmentDisplayIds : kotlin.String = initiatedShipmentDisplayIds_example // kotlin.String | The shipment's unique display id associated with the order. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro.
try {
    val result : Orders = apiInstance.getOrders(pageNumber, pageSize, orderStatus, productId, variantId, sortOrder, inventoryTypes, initiatedShipmentDisplayIds)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling OrderApi#getOrders")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling OrderApi#getOrders")
    e.printStackTrace()
}
```

### Parameters
| **pageNumber** | **kotlin.Int**| The number of page | [optional] |
| **pageSize** | **kotlin.Int**| Requested page number. Starts at 1 | [optional] |
| **orderStatus** | **kotlin.String**| To filter your Orders by a given order status&lt;br&gt;&lt;br&gt;Available values: \&quot;CREATED\&quot;, \&quot;CCAUTHORIZATIONFAILED\&quot;, \&quot;SHIPPED\&quot;, \&quot;RECEIVED\&quot;, \&quot;AUTHENTICATING\&quot;, \&quot;AUTHENTICATED\&quot;, \&quot;PAYOUTPENDING\&quot;, \&quot;PAYOUTCOMPLETED\&quot;, \&quot;SYSTEMFULFILLED\&quot;, \&quot;PAYOUTFAILED\&quot;, \&quot;SUSPENDED\&quot; | [optional] |
| **productId** | **kotlin.String**| Unique identifier for a product | [optional] |
| **variantId** | **kotlin.String**| Unique identifier for a products variant | [optional] |
| **sortOrder** | **kotlin.String**| The field by which the results are sorted. Defaults to \&quot;CREATEDAT\&quot; and can also accept \&quot;SHIPBYDATE\&quot;. | [optional] |
| **inventoryTypes** | **kotlin.String**| Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\&quot;| &#39;&#39;). The inventory types are STANDARD or FLEX. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **initiatedShipmentDisplayIds** | **kotlin.String**| The shipment&#39;s unique display id associated with the order. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro. | [optional] |

### Return type

[**Orders**](Orders.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

