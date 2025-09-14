# TGTG (Too Good To Go) Monitoring Script

This script monitors TGTG (Too Good To Go) businesses for new items and stock changes in your specified location using the Cereal Automation platform.

## Features

- **Location-based Monitoring**: Monitor businesses within a specified radius of your coordinates
- **New Item Detection**: Get notified when new items are published to TGTG
- **Stock Availability Monitoring**: Track when items become available or restock
- **Favorites Support**: Option to monitor only your favorite businesses
- **Interactive Authentication**: User-friendly email-based authentication flow
- **Proxy Support**: Optional proxy support for API requests
- **Cereal Integration**: Built on the Cereal Automation platform with advanced monitoring strategies

## Configuration

### Required Settings

- **Email**: Your TGTG account email address for authentication
- **Latitude**: Your location's latitude coordinate (e.g., 52.3676 for Amsterdam)
- **Longitude**: Your location's longitude coordinate (e.g., 4.9041 for Amsterdam)

### Optional Settings

- **Search Radius (meters)**: Search radius in meters around your location (default: 50000 = 50km)
- **Favorites Only**: Only monitor businesses you've marked as favorites (default: false)
- **Proxies**: Optional proxy configuration for API requests

## Setup

### 1. Authentication

The script uses an interactive authentication flow that automatically handles TGTG login:

1. **Automatic Login Check**: The script first attempts to login using stored credentials
2. **Email Authentication**: If login fails, an authentication email is sent to your TGTG account
3. **User Interaction**: The script displays clear instructions and waits for your confirmation
4. **Email Link**: Open the email on your PC/computer (NOT on your phone) and click the authentication link
5. **Continue Button**: Press the "Continue" button in the script interface after clicking the email link
6. **Automatic Monitoring**: Once authenticated, monitoring begins automatically

#### Important Authentication Notes
- Always open the authentication email on a PC/computer, not on a phone with the TGTG app
- The script will wait for you to click the "Continue" button after authentication
- Authentication tokens are automatically stored and refreshed as needed
- The authentication flow is handled by `TgtgLoginCommand` and `TgtgAuthPollCommand`

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

The script uses a command-based architecture with the following flow:

1. **Authentication** (`TgtgLoginCommand`): Checks existing credentials or initiates email authentication
2. **User Interaction** (`TgtgAuthPollCommand`): Waits for user to complete email authentication
3. **Monitoring** (`MonitorCommand`): Continuously monitors TGTG for changes using advanced strategies
4. **Data Processing**: Converts TGTG API data to standardized monitoring format
5. **Change Detection**: Compares current state with previous runs to identify changes
6. **Notifications**: Sends alerts when new items or stock changes are detected

### Monitoring Strategies

The script uses two sophisticated monitoring strategies:

- **New Item Detection**: Identifies items published since the last monitoring cycle
- **Stock Availability Monitoring**: Tracks when items become available or restock, including variant changes

## Monitored Information

For each TGTG item, the script tracks:

- **Item Name**: Name of the food item or business
- **Price**: Cost of the item (including taxes)
- **Stock Status**: Whether items are available and how many
- **Distance**: How far the business is from your location
- **Pickup Time**: When you can collect the item
- **Store Information**: Business name, address, and description
- **Item Details**: Food category, dietary information, handling instructions
- **Variants**: Different sizes or types of the same item

## Notifications

The script sends notifications for:

- **New Items**: When new items are published to TGTG (based on publish date)
- **Stock Availability**: When items become available or restock
- **Variant Changes**: When new variants appear in stock or existing variants restock
- **Initial Run**: Optionally notifies about items already in stock on first run

## Error Handling

The script handles common issues gracefully:

- **Authentication Failures**: Clear error messages and retry mechanisms for login issues
- **User Interaction**: Proper handling of user interaction failures during authentication
- **Network Issues**: Automatic retries for failed API requests
- **Missing Data**: Continues monitoring even if some data is unavailable
- **API Limits**: Respects TGTG API rate limits and implements proper error handling
- **Unrecoverable Exceptions**: Clear error messages for critical failures that require user intervention

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
Search Radius (meters): 25000 (25km)
Favorites Only: false
Proxies: (optional)
```

This configuration will monitor all TGTG businesses within 25km of Amsterdam city center using the default monitoring strategies for new items and stock availability.

## Technical Implementation

The script is built using:

- **Cereal Automation Platform**: Provides the monitoring framework and command execution
- **Kotlin**: Primary programming language with coroutines for async operations
- **Command Pattern**: Modular command-based architecture for authentication and monitoring
- **Monitoring Strategies**: Pluggable strategies for different types of change detection
- **TGTG API Integration**: Direct integration with TGTG's API for real-time data
