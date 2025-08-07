package com.cereal.command.monitor.data.tgtg

import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.Proxy
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Example usage of the TGTG API Client
 *
 * This class demonstrates how to:
 * 1. Initialize the API client
 * 2. Authenticate via email
 * 3. Poll for authentication completion
 * 4. Login with stored credentials
 * 5. Fetch favorite businesses
 * 6. Update app version
 */
class TgtgExample(
    private val logRepository: LogRepository,
    private val httpProxy: Proxy? = null
) {

    suspend fun demonstrateFullFlow(email: String) {
        // 1. Create configuration
        val config = TgtgConfig(email = email)

        // 2. Create API client
        val apiClient = TgtgApiClient(
            logRepository = logRepository,
            config = config,
            httpProxy = httpProxy
        )

        // 3. Create app version updater
        val versionUpdater = TgtgAppVersionUpdater(
            logRepository = logRepository,
            httpProxy = httpProxy
        )

        try {
            // 4. Update app version (optional but recommended)
            logRepository.info("Updating app version...")
            versionUpdater.updateAppVersion(config)

            // 5. Start authentication process
            logRepository.info("Starting authentication for email: $email")
            val authResponse = apiClient.authByEmail()

            val pollingId = authResponse.pollingId
            if (pollingId == null) {
                logRepository.info("Failed to get polling ID from auth response")
                return
            }

            logRepository.info("Authentication email sent. Polling ID: $pollingId")
            logRepository.info("Please check your email and click the login link.")

            // 6. Poll for authentication completion
            var authCompleted = false
            var attempts = 0
            val maxAttempts = 30 // 5 minutes with 10-second intervals

            while (!authCompleted && attempts < maxAttempts) {
                attempts++
                logRepository.info("Polling for authentication completion (attempt $attempts/$maxAttempts)...")

                try {
                    val pollResponse = apiClient.authPoll(pollingId)

                    if (pollResponse.accessToken != null && pollResponse.refreshToken != null) {
                        logRepository.info("Authentication successful!")
                        authCompleted = true

                        // Display user information if available
                        pollResponse.startupData?.user?.let { user ->
                            logRepository.info("Logged in as: ${user.name} (${user.email})")
                        }
                    } else {
                        logRepository.info("Authentication not yet completed. Waiting...")
                        delay(10.seconds)
                    }
                } catch (e: Exception) {
                    logRepository.info("Polling attempt failed: ${e.message}")
                    delay(10.seconds)
                }
            }

            if (!authCompleted) {
                logRepository.info("Authentication timed out. Please try again.")
                return
            }

            // 7. Test login with stored credentials
            logRepository.info("Testing login with stored credentials...")
            val loginSuccess = apiClient.login()
            if (loginSuccess) {
                logRepository.info("Login successful!")
            } else {
                logRepository.info("Login failed!")
                return
            }

            // 8. Fetch favorite businesses
            logRepository.info("Fetching favorite businesses...")
            val businesses = apiClient.listFavoriteBusinesses()

            if (businesses != null) {
                logRepository.info("Found ${businesses.items.size} favorite businesses:")

                businesses.items.forEach { business ->
                    val storeName = business.store?.storeName ?: "Unknown Store"
                    val itemsAvailable = business.itemsAvailable
                    val distance = String.format("%.2f", business.distance / 1000.0) // Convert to km
                    val price = business.item?.price?.let { price ->
                        "${price.minorUnits / 100.0} ${price.code}"
                    } ?: "Unknown price"

                    logRepository.info("- $storeName: $itemsAvailable items available, ${distance}km away, $price")
                }
            } else {
                logRepository.info("Failed to fetch favorite businesses or no businesses found")
            }

        } catch (e: Exception) {
            logRepository.info("Error during TGTG API demonstration: ${e.message}")
        }
    }

    suspend fun demonstrateLoginOnly(email: String) {
        val config = TgtgConfig(email = email)
        val apiClient = TgtgApiClient(
            logRepository = logRepository,
            config = config,
            httpProxy = httpProxy
        )

        try {
            // Attempt login with existing credentials
            logRepository.info("Attempting login for: $email")
            val loginSuccess = apiClient.login()

            if (loginSuccess) {
                logRepository.info("Login successful!")

                // Fetch businesses to verify the session works
                val businesses = apiClient.listFavoriteBusinesses()
                if (businesses != null) {
                    logRepository.info("Successfully fetched ${businesses.items.size} favorite businesses")
                } else {
                    logRepository.info("Login succeeded but failed to fetch businesses")
                }
            } else {
                logRepository.info("Login failed. You may need to authenticate first using demonstrateFullFlow()")
            }
        } catch (e: Exception) {
            logRepository.info("Error during login: ${e.message}")
        }
    }
}
