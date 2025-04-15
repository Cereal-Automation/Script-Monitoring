
# OperationApi

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **listingId** | **kotlin.String** | Unique ID for this listing |  |
| **operationId** | **kotlin.String** | Unique ID for this operation. |  |
| **operationType** | [**OperationType**](OperationType.md) |  |  |
| **operationStatus** | [**OperationStatus**](OperationStatus.md) |  |  |
| **operationInitiatedBy** | [**OperationInitiatedBy**](OperationInitiatedBy.md) |  |  |
| **operationInitiatedVia** | [**SupportedOperationInitiatedVia**](SupportedOperationInitiatedVia.md) |  |  |
| **createdAt** | **kotlin.String** | When the listing was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z |  |
| **updatedAt** | **kotlin.String** | When this listing was last updated in UTC.  Represented as ISO 8601 format like 2022-01-18T20:20:39Z |  |
| **changes** | [**DiffRecursivePartialListingChangeableFields**](DiffRecursivePartialListingChangeableFields.md) |  |  |
| **error** | **kotlin.String** | Error message if the creation failed |  |



