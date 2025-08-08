# TGTG (Too Good To Go) Monitoring Script

This script monitors TGTG (Too Good To Go) businesses for new items and stock changes in your specified location.

## Features

- **Location-based Monitoring**: Monitor businesses within a specified radius of your coordinates
- **New Item Alerts**: Get notified when new items become available
- **Stock Change Monitoring**: Track when items go in/out of stock or change availability
- **Favorites Support**: Option to monitor only your favorite businesses
- **Automatic Authentication**: Handles TGTG API authentication and token refresh
- **Proxy Support**: Optional proxy support for API requests

## Configuration

### Required Settings

- **Email**: Your TGTG account email address
- **Latitude**: Your location's latitude coordinate (e.g., 52.3676 for Amsterdam)
- **Longitude**: Your location's longitude coordinate (e.g., 4.9041 for Amsterdam)

### Optional Settings

- **Search Radius**: Search radius in meters around your location (default: 50000 = 50km)
- **Favorites Only**: Only monitor businesses you've marked as favorites (default: false)
- **Monitor New Items**: Send notifications for new items (default: true)
- **Monitor Stock Changes**: Send notifications for stock changes (default: true)
- **Monitor Interval**: How often to check for updates in seconds (default: 15-30 seconds)
- **Proxies**: Optional proxy configuration for API requests

## Setup

### 1. Authentication

The script automatically handles TGTG authentication:

1. **First Run**: The script will automatically send an authentication email to your TGTG account
2. **Email Instructions**: Check your email for a message from Too Good To Go
3. **Click Link**: Open the email on your PC/computer (NOT on your phone) and click the authentication link
4. **Auto-Continue**: The script will automatically detect authentication and continue monitoring
5. **Subsequent Runs**: The script will use stored credentials for future runs

#### Important Authentication Notes
- Always open the authentication email on a PC/computer, not on a phone with the TGTG app
- The script will wait up to 5 minutes for you to click the authentication link
- Authentication tokens are automatically stored and refreshed as needed

#### Manual Authentication (Optional)
If you prefer manual setup, you can authenticate beforehand:

```kotlin
// Run this once to authenticate manually
val example = TgtgExample(logRepository, httpProxy)
example.demonstrateFullFlow("your-email@example.com")
```

**Interactive authentication is recommended** as it's more user-friendly and handles the entire flow automatically.

### 2. Script Configuration

Configure the script with your location and preferences:

- **Email**: The same email you used for authentication
- **Latitude/Longitude**: Your location coordinates (you can find these using Google Maps)
- **Search Radius**: How far to search for businesses (in meters)

### 3. Finding Your Coordinates

To find your latitude and longitude:
1. Open Google Maps
2. Right-click on your location
3. Click on the coordinates that appear
4. Use these values in the script configuration

## How It Works

The script:

1. **Authenticates** with the TGTG API (automatically if interactive auth is enabled)
2. **Fetches** businesses within your specified radius
3. **Converts** TGTG data to the standard monitoring format
4. **Compares** current items with previous runs to detect changes
5. **Sends notifications** when new items appear or stock changes

### Interactive Authentication Flow

When interactive authentication is enabled and login fails:

1. **Email Sent**: Authentication email is sent to your TGTG account
2. **User Notification**: Script displays clear instructions
3. **Waiting Period**: Script waits up to 5 minutes for you to click the email link
4. **Auto-Continue**: Once authenticated, monitoring begins automatically
5. **Error Handling**: Clear error messages if authentication fails

## Monitored Information

For each TGTG item, the script tracks:

- **Item Name**: Name of the food item or business
- **Price**: Cost of the item (including taxes)
- **Stock Status**: Whether items are available and how many
- **Distance**: How far the business is from your location
- **Pickup Time**: When you can collect the item
- **Store Information**: Business name, address, and description
- **Item Details**: Food category, dietary information, handling instructions

## Notifications

The script can send notifications for:

- **New Items**: When a business adds new items to TGTG
- **Stock Changes**: When items become available or go out of stock
- **Sales Window**: When items enter or leave their sales window

## Error Handling

The script handles common issues gracefully:

- **Authentication Failures**: Logs clear messages if login fails
- **Network Issues**: Retries failed requests automatically
- **Missing Data**: Continues monitoring even if some data is unavailable
- **API Limits**: Respects TGTG API rate limits

## Tips

- **Location Accuracy**: Use precise coordinates for better results
- **Radius Size**: Start with a smaller radius (5-10km) to avoid too many notifications
- **Peak Times**: TGTG is most active during meal times and business closing hours
- **Favorites**: Mark businesses as favorites in the TGTG app to focus monitoring

## Troubleshooting

### Authentication Issues
- Ensure you've completed the initial authentication flow
- Check that your email address is correct
- Try running the authentication example again

### No Items Found
- Verify your coordinates are correct
- Increase your search radius
- Check if there are TGTG businesses in your area

### Too Many Notifications
- Reduce your search radius
- Enable "Favorites Only" mode
- Increase the monitor interval

## Example Configuration

```
Email: your-email@example.com
Latitude: 52.3676 (Amsterdam)
Longitude: 4.9041 (Amsterdam)
Search Radius: 25000 (25km)
Favorites Only: false
Monitor New Items: true
Monitor Stock Changes: true
Enable Interactive Authentication: true
Monitor Interval: 30 (seconds)
```

This configuration will monitor all TGTG businesses within 25km of Amsterdam city center, checking every 30 seconds for new items and stock changes.
