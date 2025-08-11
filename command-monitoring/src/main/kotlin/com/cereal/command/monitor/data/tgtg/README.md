# TGTG (Too Good To Go) API Client

This is a Kotlin implementation of the Too Good To Go API client, converted from the original JavaScript version. It provides functionality to authenticate with the TGTG API and fetch favorite businesses.

## Features

- **Email Authentication**: Start authentication process via email
- **Polling Authentication**: Complete authentication by polling for user confirmation
- **Session Management**: Automatic token refresh and session handling
- **Favorite Businesses**: Fetch list of favorite businesses with availability information
- **App Version Updates**: Automatically update app version from Google Play Store
- **Proxy Support**: Optional HTTP proxy support
- **Comprehensive Logging**: Detailed logging through LogRepository

## Components

### TgtgApiClient
Main API client class that handles all TGTG API interactions.

### TgtgConfig
Configuration class that stores:
- User email
- Device type (default: "ANDROID")
- App version (default: "23.2.1")
- Correlation ID (auto-generated UUID)
- Session tokens

### TgtgAppVersionDataSource
Data source class to fetch the latest TGTG app version from Google Play Store.

### TgtgItemRepository
Repository implementation that integrates with the monitoring system's ItemRepository interface. Converts TGTG business items into the standard Item format used by the monitoring system.

### Data Models
- **AuthModels**: Authentication request/response models
- **FavoriteBusinessesModels**: Business listing models with detailed store and item information

## Usage

### Basic Authentication Flow

```kotlin
// 1. Create configuration
val config = TgtgConfig(email = "your-email@example.com")

// 2. Create API client
val apiClient = TgtgApiClient(
    logRepository = logRepository,
    config = config,
    preferenceComponent = preferenceComponent,
    httpProxy = null // Optional proxy
)

// 3. Start authentication
val authResponse = apiClient.authByEmail()
val pollingId = authResponse.pollingId

// 4. User checks email and clicks login link

// 5. Poll for authentication completion
val pollResponse = apiClient.authPoll(pollingId)
// Session is automatically created if successful

// 6. Fetch favorite businesses
val businesses = apiClient.listFavoriteBusinesses()
```

### Login with Existing Session

```kotlin
val config = TgtgConfig(email = "your-email@example.com")
val apiClient = TgtgApiClient(logRepository, config, preferenceComponent)

// Attempt login with stored refresh token
val loginSuccess = apiClient.login()
if (loginSuccess) {
    val businesses = apiClient.listFavoriteBusinesses()
}
```

### Get App Version

```kotlin
val versionDataSource = TgtgAppVersionDataSource(logRepository)
try {
    val appVersion = versionDataSource.getAppVersion()
    config.appVersion = appVersion
} catch (e: TgtgAppVersionException) {
    // Handle the case where app version cannot be determined
    // Will use the default app version from config
    println("Failed to get app version: ${e.message}")
}
```

### Using TgtgItemRepository

```kotlin
// 1. Create and authenticate API client
val config = TgtgConfig(email = "your-email@example.com")
val apiClient = TgtgApiClient(logRepository, config)
val loginSuccess = apiClient.login()

// 2. Create repository with location parameters
val repository = TgtgItemRepository(
    tgtgApiClient = apiClient,
    latitude = 52.3676, // Amsterdam coordinates
    longitude = 4.9041,
    radius = 50000, // 50km radius in meters
    favoritesOnly = false // Include all businesses, not just favorites
)

// 3. Fetch items using the standard ItemRepository interface
val page = repository.getItems(null)
page.items.forEach { item ->
    println("${item.name}: ${item.properties.joinToString(", ")}")
}
```

### Complete Example

See `TgtgExample.kt` for a comprehensive demonstration of the full authentication and data fetching flow.

## API Endpoints

The client interacts with the following TGTG API endpoints:

- `POST auth/v5/authByEmail` - Start email authentication
- `POST auth/v5/authByRequestPollingId` - Poll for authentication completion
- `POST token/v1/refresh` - Refresh access token
- `POST item/v8/` - Fetch favorite businesses

## Configuration

### Required
- **email**: Your TGTG account email address

### Optional
- **deviceType**: Device type (default: "ANDROID")
- **appVersion**: TGTG app version (default: "23.2.1", can be auto-updated)
- **httpProxy**: HTTP proxy configuration
- **timeout**: Request timeout (default: 30 seconds)

## Error Handling

The client includes comprehensive error handling:
- Network timeouts and retries
- Authentication failures
- Session expiration and automatic refresh
- Detailed logging of all operations

## Thread Safety

The client uses mutex locks to ensure thread-safe session management when multiple coroutines access the same client instance.

## Dependencies

This client leverages the existing project infrastructure:
- **Ktor**: HTTP client with OkHttp engine
- **Kotlinx Serialization**: JSON serialization/deserialization
- **Kotlinx Coroutines**: Async/await support
- **Project's HTTP Client**: Reuses existing `defaultHttpClient` with proxy and logging support

## Testing

Basic unit tests are provided in `TgtgApiClientTest.kt` to verify component creation and basic functionality.

## Notes

- The client follows the same patterns as other API clients in the project
- Session tokens are stored in memory and will be lost when the application restarts
- For production use, consider persisting session tokens to storage
- The app version updater scrapes Google Play Store and may need updates if the page structure changes
