
# BatchCreateListingResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **batchId** | **kotlin.String** | Unique Batch ID |  |
| **status** | **kotlin.String** | The status of the batch |  |
| **completedAt** | **kotlin.String** | When the batch fully completed |  |
| **createdAt** | **kotlin.String** | when this batch was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **updatedAt** | **kotlin.String** | When this batch was last updated in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **totalItems** | **kotlin.Double** | Total number of items in this batch |  |
| **itemStatuses** | **kotlin.collections.Map&lt;kotlin.String, kotlin.Double&gt;** | The number of items in this batch grouped by their statuses. This is a short-hand way to quickly introspect how many items are still enqued, how many succeeded or how many failed in a batch. |  |



