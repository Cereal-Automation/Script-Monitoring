# ListingsApi

All URIs are relative to *https://api.stockx.com/v2*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**activateListing**](ListingsApi.md#activateListing) | **PUT** /selling/listings/{listingId}/activate | Activate a listing |
| [**create**](ListingsApi.md#create) | **POST** /selling/listings | Create a new listing |
| [**deactivateListing**](ListingsApi.md#deactivateListing) | **PUT** /selling/listings/{listingId}/deactivate | Deactivate a listing |
| [**deleteListing**](ListingsApi.md#deleteListing) | **DELETE** /selling/listings/{listingId} | Delete a listing |
| [**findById**](ListingsApi.md#findById) | **GET** /selling/listings/{listingId} | Get single listing |
| [**findOperationById**](ListingsApi.md#findOperationById) | **GET** /selling/listings/{listingId}/operations/{operationId} | Get single listing operation |
| [**getAllListings**](ListingsApi.md#getAllListings) | **GET** /selling/listings | Get all listings |
| [**getListingOperations**](ListingsApi.md#getListingOperations) | **GET** /selling/listings/{listingId}/operations | Get all listing operations |
| [**update**](ListingsApi.md#update) | **PATCH** /selling/listings/{listingId} | Update a listing |


<a id="activateListing"></a>
# **activateListing**
> ListingAsyncOperationResponse activateListing(listingId, activateListingInput)

Activate a listing

Activate listing API allows you to activate a listing. A listing is active when it contains an available ask.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
val activateListingInput : ActivateListingInput =  // ActivateListingInput | 
try {
    val result : ListingAsyncOperationResponse = apiInstance.activateListing(listingId, activateListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#activateListing")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#activateListing")
    e.printStackTrace()
}
```

### Parameters
| **listingId** | **kotlin.String**| Unique ID for this listing | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **activateListingInput** | [**ActivateListingInput**](ActivateListingInput.md)|  | |

### Return type

[**ListingAsyncOperationResponse**](ListingAsyncOperationResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="create"></a>
# **create**
> ListingAsyncOperationResponse create(createListingInput)

Create a new listing

Create listings API allows you to create new listings. The listings correspond to an ask in the StockX UI. The listings can be for the same variant ID.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val createListingInput : CreateListingInput =  // CreateListingInput | 
try {
    val result : ListingAsyncOperationResponse = apiInstance.create(createListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#create")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#create")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **createListingInput** | [**CreateListingInput**](CreateListingInput.md)|  | |

### Return type

[**ListingAsyncOperationResponse**](ListingAsyncOperationResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deactivateListing"></a>
# **deactivateListing**
> ListingAsyncOperationResponse deactivateListing(listingId)

Deactivate a listing

Deactivate listing API allows you to deactivate a listing. A listing is deactivated when it doesn&#39;t have an ask or when it contains an expired ask.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
try {
    val result : ListingAsyncOperationResponse = apiInstance.deactivateListing(listingId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#deactivateListing")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#deactivateListing")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **listingId** | **kotlin.String**| Unique ID for this listing | |

### Return type

[**ListingAsyncOperationResponse**](ListingAsyncOperationResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="deleteListing"></a>
# **deleteListing**
> ListingAsyncOperationResponse deleteListing(listingId)

Delete a listing

Operation used to delete an existing listing by its ID.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
try {
    val result : ListingAsyncOperationResponse = apiInstance.deleteListing(listingId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#deleteListing")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#deleteListing")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **listingId** | **kotlin.String**| Unique ID for this listing | |

### Return type

[**ListingAsyncOperationResponse**](ListingAsyncOperationResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="findById"></a>
# **findById**
> ListingResponse findById(listingId)

Get single listing

Get a listing API allows you to get a listing by its ID.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
try {
    val result : ListingResponse = apiInstance.findById(listingId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#findById")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#findById")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **listingId** | **kotlin.String**| Unique ID for this listing | |

### Return type

[**ListingResponse**](ListingResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="findOperationById"></a>
# **findOperationById**
> OperationApi findOperationById(listingId, operationId)

Get single listing operation

Get listing operation API allows you to fetch a listing operation by listing ID and operation ID

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing.
val operationId : kotlin.String = operationId_example // kotlin.String | Unique ID for this operation.
try {
    val result : OperationApi = apiInstance.findOperationById(listingId, operationId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#findOperationById")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#findOperationById")
    e.printStackTrace()
}
```

### Parameters
| **listingId** | **kotlin.String**| Unique ID for this listing. | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **operationId** | **kotlin.String**| Unique ID for this operation. | |

### Return type

[**OperationApi**](OperationApi.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getAllListings"></a>
# **getAllListings**
> Listings getAllListings(pageNumber, pageSize, productIds, variantIds, batchIds, fromDate, toDate, listingStatuses, inventoryTypes, initiatedShipmentDisplayIds)

Get all listings

Get all listings API allows you to fetch all existing listings. Multiple filters are available.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val pageNumber : kotlin.Int = 1 // kotlin.Int | Requested page number. By default the page number starts at 1
val pageSize : kotlin.Int = 100 // kotlin.Int | The number of listings to return. By default the page size starts at 1.
val productIds : kotlin.String = productIds_example // kotlin.String | Comma separated list of ids. This field must not include array brackets `[]` or quotation marks (\" \" | ' ').
val variantIds : kotlin.String = variantIds_example // kotlin.String | Comma separated list of ids. This field must not include array brackets `[]` or quotation marks (\" \" | ' ').
val batchIds : kotlin.String = batchIds_example // kotlin.String | Comma separated list of ids. This field must not include array brackets `[]` or quotation marks (\" \" | ' ').
val fromDate : kotlin.String = 2022-06-08 // kotlin.String | Start date of the query
val toDate : kotlin.String = 2022-06-08 // kotlin.String | End date of the query
val listingStatuses : kotlin.String = ACTIVE // kotlin.String | Comma separated list of listing statuses. This field must not include array brackets `[]` or quotation marks (\"\" | '').<br><br>Available values: \"INACTIVE\", \"ACTIVE\", \"DELETED\", \"CANCELED\", \"MATCHED\", \"COMPLETED\"
val inventoryTypes : kotlin.String = STANDARD // kotlin.String | Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\"| ''). The inventory types are STANDARD or FLEX.
val initiatedShipmentDisplayIds : kotlin.String = initiatedShipmentDisplayIds_example // kotlin.String | The shipment's unique display id associated with the listing. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro.
try {
    val result : Listings = apiInstance.getAllListings(pageNumber, pageSize, productIds, variantIds, batchIds, fromDate, toDate, listingStatuses, inventoryTypes, initiatedShipmentDisplayIds)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#getAllListings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#getAllListings")
    e.printStackTrace()
}
```

### Parameters
| **pageNumber** | **kotlin.Int**| Requested page number. By default the page number starts at 1 | [optional] |
| **pageSize** | **kotlin.Int**| The number of listings to return. By default the page size starts at 1. | [optional] |
| **productIds** | **kotlin.String**| Comma separated list of ids. This field must not include array brackets &#x60;[]&#x60; or quotation marks (\&quot; \&quot; | &#39; &#39;). | [optional] |
| **variantIds** | **kotlin.String**| Comma separated list of ids. This field must not include array brackets &#x60;[]&#x60; or quotation marks (\&quot; \&quot; | &#39; &#39;). | [optional] |
| **batchIds** | **kotlin.String**| Comma separated list of ids. This field must not include array brackets &#x60;[]&#x60; or quotation marks (\&quot; \&quot; | &#39; &#39;). | [optional] |
| **fromDate** | **kotlin.String**| Start date of the query | [optional] |
| **toDate** | **kotlin.String**| End date of the query | [optional] |
| **listingStatuses** | **kotlin.String**| Comma separated list of listing statuses. This field must not include array brackets &#x60;[]&#x60; or quotation marks (\&quot;\&quot; | &#39;&#39;).&lt;br&gt;&lt;br&gt;Available values: \&quot;INACTIVE\&quot;, \&quot;ACTIVE\&quot;, \&quot;DELETED\&quot;, \&quot;CANCELED\&quot;, \&quot;MATCHED\&quot;, \&quot;COMPLETED\&quot; | [optional] |
| **inventoryTypes** | **kotlin.String**| Comma-separated list of inventory type(s). This field must not include array brackets [] or quotation marks (\&quot;| &#39;&#39;). The inventory types are STANDARD or FLEX. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **initiatedShipmentDisplayIds** | **kotlin.String**| The shipment&#39;s unique display id associated with the listing. Note: This is the same ID generated when a Flex inbound list is created in StockX Pro. | [optional] |

### Return type

[**Listings**](Listings.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingOperations"></a>
# **getListingOperations**
> OperationsCursorResponse getListingOperations(listingId, pageSize, cursor)

Get all listing operations

Get all listing operations API allows you to fetch a paginated list of single listing with all operations.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
val pageSize : kotlin.Int = 56 // kotlin.Int | Requested page number. Starts at 1.
val cursor : kotlin.String = cursor_example // kotlin.String | The cursor to use as a starting point
try {
    val result : OperationsCursorResponse = apiInstance.getListingOperations(listingId, pageSize, cursor)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#getListingOperations")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#getListingOperations")
    e.printStackTrace()
}
```

### Parameters
| **listingId** | **kotlin.String**| Unique ID for this listing | |
| **pageSize** | **kotlin.Int**| Requested page number. Starts at 1. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **cursor** | **kotlin.String**| The cursor to use as a starting point | [optional] |

### Return type

[**OperationsCursorResponse**](OperationsCursorResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="update"></a>
# **update**
> ListingAsyncOperationResponse update(listingId, updateListingInput)

Update a listing

Operation used to update an existing listing by its ID.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = ListingsApi()
val listingId : kotlin.String = listingId_example // kotlin.String | Unique ID for this listing
val updateListingInput : UpdateListingInput =  // UpdateListingInput | 
try {
    val result : ListingAsyncOperationResponse = apiInstance.update(listingId, updateListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ListingsApi#update")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ListingsApi#update")
    e.printStackTrace()
}
```

### Parameters
| **listingId** | **kotlin.String**| Unique ID for this listing | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **updateListingInput** | [**UpdateListingInput**](UpdateListingInput.md)|  | |

### Return type

[**ListingAsyncOperationResponse**](ListingAsyncOperationResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

