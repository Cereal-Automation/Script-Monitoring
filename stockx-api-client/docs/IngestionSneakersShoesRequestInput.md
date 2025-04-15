
# IngestionSneakersShoesRequestInput

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **category** | [**PRODUCTCATEGORIES**](PRODUCTCATEGORIES.md) |  |  |
| **productTitle** | **kotlin.String** | A string that uniquely identifies the name of the product. |  |
| **brand** | **kotlin.String** | The brand of the product. |  |
| **productImages** | **kotlin.collections.List&lt;kotlin.String&gt;** | Public facing links to the products image. |  |
| **variants** | **kotlin.collections.List&lt;kotlin.String&gt;** | The product variants available to sell. For example, for sneakers, each different size is a different variant or for electronics like iPhone, each different storage capacity is a different variant. |  |
| **retailPrice** | **kotlin.String** | The products retail price. |  |
| **releaseDate** | **kotlin.String** | The products release date. |  |
| **countryOfOrigin** | **kotlin.String** | The country in which the product was manufactured in ISO Alpha 2 Format. |  |
| **colorway** | **kotlin.String** | The combinations of colors in which the product is designed. Ex VIOLET ORE/MEDIUM ASH-BLACK-MUSLIN-BURGUNDY CRUSH. |  |
| **gtin** | **kotlin.String** | The products global trade item number. Length must be 8, 12, 13, 14 or 18. |  |
| **gender** | **kotlin.String** | The products targeted gender. |  |
| **styleCode** | **kotlin.String** | The Style Code for the product  @example \&quot;M990BK5\&quot; |  |
| **partnerProductId** | **kotlin.String** | An external ID that partners will use to reference an internal StockX catalog item if approved. Note that this is the higher level product id, not the variantId. |  [optional] |
| **tags** | **kotlin.collections.List&lt;kotlin.String&gt;** | A list of attributes or short descriptors associated with the product. |  [optional] |
| **productDescription** | **kotlin.String** | Open text field used for describing the product to the customer. |  [optional] |
| **dimensions** | **kotlin.String** | A string of the dimensions of the product. @example \&quot;2/\&quot;x4/\&quot;x6/\&quot;\&quot; |  [optional] |
| **weight** | **kotlin.String** | The weight of the product. |  [optional] |
| **material** | **kotlin.String** | Brand listed materials. |  [optional] |
| **productURL** | **kotlin.String** | Official third party product URL. |  [optional] |



