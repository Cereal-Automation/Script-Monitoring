# BatchApi

All URIs are relative to *https://api.stockx.com/v2*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createListings**](BatchApi.md#createListings) | **POST** /selling/batch/create-listing | Batch listings creation |
| [**deleteListings**](BatchApi.md#deleteListings) | **POST** /selling/batch/delete-listing | Batch listings deletion |
| [**getListingCreateBatch**](BatchApi.md#getListingCreateBatch) | **GET** /selling/batch/create-listing/{batchId} | Batch listings creation - Get Batch Status |
| [**getListingCreateBatchItems**](BatchApi.md#getListingCreateBatchItems) | **GET** /selling/batch/create-listing/{batchId}/items | Batch listings creation - Get Items |
| [**getListingDeleteBatch**](BatchApi.md#getListingDeleteBatch) | **GET** /selling/batch/delete-listing/{batchId} | Batch listings deletion - Get Batch Status |
| [**getListingDeleteBatchItems**](BatchApi.md#getListingDeleteBatchItems) | **GET** /selling/batch/delete-listing/{batchId}/items | Batch listings deletion - Get Items |
| [**getListingUpdateBatch**](BatchApi.md#getListingUpdateBatch) | **GET** /selling/batch/update-listing/{batchId} | Batch listings update - Get Batch Status |
| [**getListingUpdateBatchItems**](BatchApi.md#getListingUpdateBatchItems) | **GET** /selling/batch/update-listing/{batchId}/items | Batch listings update - Get Items |
| [**updateListings**](BatchApi.md#updateListings) | **POST** /selling/batch/update-listing | Batch listings update |


<a id="createListings"></a>
# **createListings**
> BatchCreateListingResponse createListings(batchCreateListingInput)

Batch listings creation

Create a new batch of listings

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchCreateListingInput : BatchCreateListingInput =  // BatchCreateListingInput | 
try {
    val result : BatchCreateListingResponse = apiInstance.createListings(batchCreateListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#createListings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#createListings")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchCreateListingInput** | [**BatchCreateListingInput**](BatchCreateListingInput.md)|  | |

### Return type

[**BatchCreateListingResponse**](BatchCreateListingResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteListings"></a>
# **deleteListings**
> BatchDeleteListingResponse deleteListings(batchDeleteListingInput)

Batch listings deletion

Batch delete listings API allows a user to delete up to 100 individual listings in a single API call. This API is asynchronous in nature and will return a batchId that you would need to poll using the polling APIs described later.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchDeleteListingInput : BatchDeleteListingInput =  // BatchDeleteListingInput | 
try {
    val result : BatchDeleteListingResponse = apiInstance.deleteListings(batchDeleteListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#deleteListings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#deleteListings")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchDeleteListingInput** | [**BatchDeleteListingInput**](BatchDeleteListingInput.md)|  | |

### Return type

[**BatchDeleteListingResponse**](BatchDeleteListingResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getListingCreateBatch"></a>
# **getListingCreateBatch**
> GetListingCreateBatchResponse getListingCreateBatch(batchId)

Batch listings creation - Get Batch Status

Once you are able to create a batch successfully, you need to poll the get batch status API to track the progress of the batch. This polling is necessary because all batch operations are asynchronous in nature.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | Unique Batch ID
try {
    val result : GetListingCreateBatchResponse = apiInstance.getListingCreateBatch(batchId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingCreateBatch")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingCreateBatch")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchId** | **kotlin.String**| Unique Batch ID | |

### Return type

[**GetListingCreateBatchResponse**](GetListingCreateBatchResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingCreateBatchItems"></a>
# **getListingCreateBatchItems**
> GetListingCreateBatchItemsResponse getListingCreateBatchItems(batchId, status)

Batch listings creation - Get Items

Once a batch completes successfully, you need to use the get batch items API to see the results of each item in the batch. You can also use this API at any point in time after the batch is created to see the progress of each individual item in a batch.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | The ID of batch
val status : kotlin.String = COMPLETED // kotlin.String | Status of listing
try {
    val result : GetListingCreateBatchItemsResponse = apiInstance.getListingCreateBatchItems(batchId, status)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingCreateBatchItems")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingCreateBatchItems")
    e.printStackTrace()
}
```

### Parameters
| **batchId** | **kotlin.String**| The ID of batch | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **status** | **kotlin.String**| Status of listing | [optional] |

### Return type

[**GetListingCreateBatchItemsResponse**](GetListingCreateBatchItemsResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingDeleteBatch"></a>
# **getListingDeleteBatch**
> GetListingDeleteBatchResponse getListingDeleteBatch(batchId)

Batch listings deletion - Get Batch Status

Once you are able to create a batch successfully, you need to poll the get batch status API to track the progress of the batch. This polling is necessary because all batch operations are asynchronous in nature.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | Unique Batch ID
try {
    val result : GetListingDeleteBatchResponse = apiInstance.getListingDeleteBatch(batchId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingDeleteBatch")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingDeleteBatch")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchId** | **kotlin.String**| Unique Batch ID | |

### Return type

[**GetListingDeleteBatchResponse**](GetListingDeleteBatchResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingDeleteBatchItems"></a>
# **getListingDeleteBatchItems**
> GetListingDeleteBatchItemsResponse getListingDeleteBatchItems(batchId, status)

Batch listings deletion - Get Items

Once a batch completes successfully, you need to use the get batch items API to see the results of each item in the batch. You can also use this API at any point in time after the batch is created to see the progress of each individual item in a batch.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | Unique Batch ID
val status : kotlin.String = COMPLETED // kotlin.String | 
try {
    val result : GetListingDeleteBatchItemsResponse = apiInstance.getListingDeleteBatchItems(batchId, status)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingDeleteBatchItems")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingDeleteBatchItems")
    e.printStackTrace()
}
```

### Parameters
| **batchId** | **kotlin.String**| Unique Batch ID | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **status** | **kotlin.String**|  | [optional] |

### Return type

[**GetListingDeleteBatchItemsResponse**](GetListingDeleteBatchItemsResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingUpdateBatch"></a>
# **getListingUpdateBatch**
> GetListingUpdateBatchResponse getListingUpdateBatch(batchId)

Batch listings update - Get Batch Status

Once you are able to create a batch successfully, you need to poll the get batch status API to track the progress of the batch. This polling is necessary because all batch operations are asynchronous in nature.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | Unique Batch ID
try {
    val result : GetListingUpdateBatchResponse = apiInstance.getListingUpdateBatch(batchId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingUpdateBatch")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingUpdateBatch")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchId** | **kotlin.String**| Unique Batch ID | |

### Return type

[**GetListingUpdateBatchResponse**](GetListingUpdateBatchResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getListingUpdateBatchItems"></a>
# **getListingUpdateBatchItems**
> GetListingUpdateBatchItemsResponse getListingUpdateBatchItems(batchId, status)

Batch listings update - Get Items

Once a batch completes successfully, you need to use the get batch items API to see the results of each item in the batch. You can also use this API at any point in time after the batch is created to see the progress of each individual item in a batch

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchId : kotlin.String = batchId_example // kotlin.String | Unique Batch ID
val status : kotlin.String = COMPLETED // kotlin.String | Status of listing
try {
    val result : GetListingUpdateBatchItemsResponse = apiInstance.getListingUpdateBatchItems(batchId, status)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#getListingUpdateBatchItems")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#getListingUpdateBatchItems")
    e.printStackTrace()
}
```

### Parameters
| **batchId** | **kotlin.String**| Unique Batch ID | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **status** | **kotlin.String**| Status of listing | [optional] |

### Return type

[**GetListingUpdateBatchItemsResponse**](GetListingUpdateBatchItemsResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="updateListings"></a>
# **updateListings**
> BatchUpdateListingResponse updateListings(batchUpdateListingInput)

Batch listings update

Batch update listings API allows a user to update up to 100 individual listings in a single API call. This API is asynchronous in nature and will return a batchId that you would need to poll using the polling APIs described later.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = BatchApi()
val batchUpdateListingInput : BatchUpdateListingInput =  // BatchUpdateListingInput | 
try {
    val result : BatchUpdateListingResponse = apiInstance.updateListings(batchUpdateListingInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatchApi#updateListings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatchApi#updateListings")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batchUpdateListingInput** | [**BatchUpdateListingInput**](BatchUpdateListingInput.md)|  | |

### Return type

[**BatchUpdateListingResponse**](BatchUpdateListingResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

