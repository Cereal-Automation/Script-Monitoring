# CatalogApi

All URIs are relative to *https://api.stockx.com/v2*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getProduct**](CatalogApi.md#getProduct) | **GET** /catalog/products/{productId} | Get single product |
| [**getProductMarketData**](CatalogApi.md#getProductMarketData) | **GET** /catalog/products/{productId}/market-data | Get market data for a product |
| [**getVariant**](CatalogApi.md#getVariant) | **GET** /catalog/products/{productId}/variants/{variantId} | Get single product variant |
| [**getVariantMarketData**](CatalogApi.md#getVariantMarketData) | **GET** /catalog/products/{productId}/variants/{variantId}/market-data | Get market data for a variant |
| [**getVariants**](CatalogApi.md#getVariants) | **GET** /catalog/products/{productId}/variants | Get all product variants |
| [**ingestion**](CatalogApi.md#ingestion) | **POST** /catalog/ingestion | Post - Create a Catalog Ingestion Job (Beta) |
| [**ingestionItems**](CatalogApi.md#ingestionItems) | **GET** /catalog/ingestion/{ingestionId} | Get - Catalog Ingestion Status (Beta) |
| [**search**](CatalogApi.md#search) | **GET** /catalog/search | Search catalog |


<a id="getProduct"></a>
# **getProduct**
> Product getProduct(productId)

Get single product

Get product details API allows you to fetch details for a single product

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
try {
    val result : Product = apiInstance.getProduct(productId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#getProduct")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#getProduct")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productId** | **kotlin.String**| Unique identifier for a product | |

### Return type

[**Product**](Product.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getProductMarketData"></a>
# **getProductMarketData**
> kotlin.collections.List&lt;VariantMarketData&gt; getProductMarketData(productId, currencyCode)

Get market data for a product

Get Market Data API allows you to obtain basic market data - the highest Bid and lowest Ask amount for all variants of given product. You may notice discrepancies in the values for sellFasterAmount and earnMoreAmount between this and the ‘Get market data for a variant&#x60; API. This is because this API does not take into account any live asks you currently have for each individual variant, while the &#39;Get market data for a variant’ API does.&lt;br/&gt; &lt;b&gt;Note:&lt;/b&gt; &lt;li&gt;Based on your region, the response object may vary.&lt;/li&gt;&lt;/ul&gt;

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
val currencyCode : CurrencyCode =  // CurrencyCode | The currency code this product is being listed in.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
try {
    val result : kotlin.collections.List<VariantMarketData> = apiInstance.getProductMarketData(productId, currencyCode)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#getProductMarketData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#getProductMarketData")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.String**| Unique identifier for a product | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **currencyCode** | [**CurrencyCode**](.md)| The currency code this product is being listed in.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; | [optional] [enum: AUD, BRL, CAD, CHF, CLP, CNY, DKK, EUR, GBP, HKD, HUF, IDR, ILS, JPY, KRW, KWD, MOP, MXN, MYR, NOK, NZD, PEN, PHP, PLN, SEK, SGD, THB, TWD, USD, VND] |

### Return type

[**kotlin.collections.List&lt;VariantMarketData&gt;**](VariantMarketData.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getVariant"></a>
# **getVariant**
> ProductVariantDetails getVariant(productId, variantId)

Get single product variant

Get variant details API allows you to fetch the details of a single variant for a given product. If the product id doesn&#39;t contain the specified variant, a validation error will be returned.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
val variantId : kotlin.String = variantId_example // kotlin.String | Unique identifier for a products variant
try {
    val result : ProductVariantDetails = apiInstance.getVariant(productId, variantId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#getVariant")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#getVariant")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.String**| Unique identifier for a product | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **variantId** | **kotlin.String**| Unique identifier for a products variant | |

### Return type

[**ProductVariantDetails**](ProductVariantDetails.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getVariantMarketData"></a>
# **getVariantMarketData**
> VariantMarketData getVariantMarketData(productId, variantId, currencyCode, country)

Get market data for a variant

Get Market Data API allows you to obtain basic market data - the highest Bid and lowest Ask amount for a given variant. If the product id doesn&#39;t contain the specified variant, a validation error will be returned.&lt;br/&gt; &lt;b&gt;Note:&lt;/b&gt; &lt;ul&gt;&lt;li&gt;We have deprecated the country param as the market data will now be based on your market.&lt;/li&gt; &lt;li&gt;Based on your region, the response object may vary.&lt;/li&gt;&lt;/ul&gt;

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
val variantId : kotlin.String = variantId_example // kotlin.String | Unique identifier for a products variant
val currencyCode : CurrencyCode =  // CurrencyCode | The currency code this product is being listed in.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
val country : kotlin.String = country_example // kotlin.String | ISO Alpha-2 code representing the country you need the market data for. If not provided, will default to your country.
try {
    val result : VariantMarketData = apiInstance.getVariantMarketData(productId, variantId, currencyCode, country)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#getVariantMarketData")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#getVariantMarketData")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.String**| Unique identifier for a product | |
| **variantId** | **kotlin.String**| Unique identifier for a products variant | |
| **currencyCode** | [**CurrencyCode**](.md)| The currency code this product is being listed in.&lt;br&gt;&lt;br&gt;Available values: \&quot;AUD\&quot;, \&quot;CAD\&quot;, \&quot;CHF\&quot;, \&quot;EUR\&quot;, \&quot;GBP\&quot;, \&quot;HKD\&quot;, \&quot;JPY\&quot;, \&quot;KRW\&quot;, \&quot;MXN\&quot;, \&quot;NZD\&quot;, \&quot;SGD\&quot;, \&quot;USD\&quot; | [optional] [enum: AUD, BRL, CAD, CHF, CLP, CNY, DKK, EUR, GBP, HKD, HUF, IDR, ILS, JPY, KRW, KWD, MOP, MXN, MYR, NOK, NZD, PEN, PHP, PLN, SEK, SGD, THB, TWD, USD, VND] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **country** | **kotlin.String**| ISO Alpha-2 code representing the country you need the market data for. If not provided, will default to your country. | [optional] |

### Return type

[**VariantMarketData**](VariantMarketData.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getVariants"></a>
# **getVariants**
> kotlin.collections.List&lt;ProductVariant&gt; getVariants(productId)

Get all product variants

Get product variants API allows you to get all the different variants of a given product.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val productId : kotlin.String = productId_example // kotlin.String | Unique identifier for a product
try {
    val result : kotlin.collections.List<ProductVariant> = apiInstance.getVariants(productId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#getVariants")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#getVariants")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productId** | **kotlin.String**| Unique identifier for a product | |

### Return type

[**kotlin.collections.List&lt;ProductVariant&gt;**](ProductVariant.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="ingestion"></a>
# **ingestion**
> IngestionControllerResponse ingestion(catalogIngestionInput)

Post - Create a Catalog Ingestion Job (Beta)

&lt;b&gt;Seamlessly Integrate Catalog Data into Our Platform.&lt;/b&gt;&lt;br&gt;To create an ingestion job, you need to provide a set of catalog attributes. This API is asynchronous and will return an IngestionID that will require polling.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val catalogIngestionInput : CatalogIngestionInput =  // CatalogIngestionInput | 
try {
    val result : IngestionControllerResponse = apiInstance.ingestion(catalogIngestionInput)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#ingestion")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#ingestion")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **catalogIngestionInput** | [**CatalogIngestionInput**](CatalogIngestionInput.md)|  | |

### Return type

[**IngestionControllerResponse**](IngestionControllerResponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="ingestionItems"></a>
# **ingestionItems**
> GetIngestionItemsReponse ingestionItems(ingestionId, status)

Get - Catalog Ingestion Status (Beta)

&lt;b&gt;Monitor Your Data Integration Status.&lt;/b&gt;&lt;br&gt;Once you create an ingestion job successfully, you need to poll the get catalog ingestion API to track the progress, whether the entire job or individual updates.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val ingestionId : kotlin.String = ingestionId_example // kotlin.String | Unique ID used to return the status of an ingestion job.
val status : kotlin.String = status_example // kotlin.String | The status of the ingestion job. In review, Completed, Rejected.
try {
    val result : GetIngestionItemsReponse = apiInstance.ingestionItems(ingestionId, status)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#ingestionItems")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#ingestionItems")
    e.printStackTrace()
}
```

### Parameters
| **ingestionId** | **kotlin.String**| Unique ID used to return the status of an ingestion job. | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **status** | **kotlin.String**| The status of the ingestion job. In review, Completed, Rejected. | [optional] |

### Return type

[**GetIngestionItemsReponse**](GetIngestionItemsReponse.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="search"></a>
# **search**
> Search search(query, pageNumber, pageSize)

Search catalog

Search catalog API allows you to search the StockX catalog via freeform text. The output is a paginated list of products that match the search term provided in the API call.

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import com.cereal.stockx.api.model.*

val apiInstance = CatalogApi()
val query : kotlin.String = nike // kotlin.String | Specifies a keyword search as a String.
val pageNumber : kotlin.Int = 1 // kotlin.Int | Requested page number. By default, the page number starts at 1.
val pageSize : kotlin.Int = 10 // kotlin.Int | The number of products to return. By default, the page size starts at 1.
try {
    val result : Search = apiInstance.search(query, pageNumber, pageSize)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CatalogApi#search")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CatalogApi#search")
    e.printStackTrace()
}
```

### Parameters
| **query** | **kotlin.String**| Specifies a keyword search as a String. | |
| **pageNumber** | **kotlin.Int**| Requested page number. By default, the page number starts at 1. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **pageSize** | **kotlin.Int**| The number of products to return. By default, the page size starts at 1. | [optional] |

### Return type

[**Search**](Search.md)

### Authorization


Configure api_key:
    ApiClient.apiKey["x-api-key"] = ""
    ApiClient.apiKeyPrefix["x-api-key"] = ""
Configure jwt:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

