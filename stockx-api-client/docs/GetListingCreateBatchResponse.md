
# GetListingCreateBatchResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **batchId** | **kotlin.String** | Stringified UUIDv4. See [RFC 4112](https://tools.ietf.org/html/rfc4122) |  |
| **status** | **kotlin.String** | The status of this batch |  |
| **completedAt** | **kotlin.String** | When this batch fully completed. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **createdAt** | **kotlin.String** | When this batch was created. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **updatedAt** | **kotlin.String** | When this batch was last updated. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **totalItems** | **kotlin.Double** | Total number of items in this batch |  |
| **itemStatuses** | **kotlin.collections.Map&lt;kotlin.String, kotlin.Double&gt;** | The number of items in this batch grouped by their statuses. This is a short-hand way to quickly introspect how many items are still enqued, how many succeeded or how many failed in a batch. |  |



