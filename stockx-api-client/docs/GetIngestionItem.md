
# GetIngestionItem

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **ingestionId** | **kotlin.String** | Unique ID used to return the status of an ingestion job. |  |
| **status** | [**INGESTIONITEMSTATUS**](INGESTIONITEMSTATUS.md) |  |  |
| **input** | [**GetIngestionItemInput**](GetIngestionItemInput.md) |  |  |
| **result** | [**IngestionItemResult**](IngestionItemResult.md) | The result of the ingestion job if the status is completed. |  |
| **partnerProductId** | **kotlin.String** | The external partner ID provided in the request. |  |
| **rejectionDetails** | [**IngestionItemRejectionDetails**](IngestionItemRejectionDetails.md) | The details about its rejection if REJECTED |  |
| **createdAt** | **kotlin.String** | Timestamp when the resource was created. |  |
| **updatedAt** | **kotlin.String** | Timestamp when the resource was last updated. |  |



