# StockX API Client Module

This module uses the [`openapi-generator-gradle-plugin`](https://github.com/OpenAPITools/openapi-generator) to automatically generate an API client from an OpenAPI specification file.

## Overview

This module is designed to be included as a dependency in other Gradle modules, enabling you to reuse your API client across multiple parts of your project.

## Updating api client

### 1. OpenAPI Specification

Place the updated Stockx OpenAPI JSON file ([found here](https://developer.stockx.com/openapi/reference/overview)) inside the root `specs/` directory: `/specs/stockx.json`.

## 2. Generating the Client

Regenerate the API client from the spec by running:

```bash
./gradlew :api-client:openApiGenerate
```

This will regenerate the client code under: `stockx-api-client/build/generated/src/main/kotlin`
