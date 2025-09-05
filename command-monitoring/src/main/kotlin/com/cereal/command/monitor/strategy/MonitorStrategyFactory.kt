package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.data.common.httpclient.plugin.RateLimiterPlugin
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.stockx.OAuthAuthenticator
import com.cereal.command.monitor.data.stockx.OAuthInterceptor
import com.cereal.command.monitor.data.stockx.OAuthTokenProvider
import com.cereal.command.monitor.data.stockx.StockXMarketItemRepository
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.stockx.api.CatalogApi
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object MonitorStrategyFactory {
    fun priceDropMonitorStrategy(): MonitorStrategy = PriceDropMonitorStrategy()

    fun stockAvailableMonitorStrategy(): MonitorStrategy = StockAvailableMonitorStrategy()

    fun stockChangedMonitorStrategy(): MonitorStrategy = StockChangedMonitorStrategy()

    fun newItemAvailableMonitorStrategy(since: Instant): MonitorStrategy = NewItemAvailableMonitorStrategy(since)

    fun marketPriceComparisonStrategy(
        userInteractionComponent: UserInteractionComponent,
        clientId: String,
        clientSecret: String,
    ): MonitorStrategy {
        val catalogApi =
            CatalogApi(
                baseUrl = "https://api.stockx.com/v2",
                httpClientEngine =
                    OkHttp.create {
                        config {
                            followRedirects(true)

                            val tokenProvider =
                                OAuthTokenProvider(
                                    userInteractionComponent,
                                    clientId = clientId,
                                    clientSecret = clientSecret,
                                    redirectUri = "http://localhost/stockx-auth-callback",
                                    tokenUrl = "https://accounts.stockx.com/oauth/token",
                                    authorizationUrl = "https://accounts.stockx.com/authorize",
                                )
                            addInterceptor(OAuthInterceptor(tokenProvider))
                            authenticator(OAuthAuthenticator(tokenProvider))
                        }
                    },
                httpClientConfig = {
                    it.install(ContentNegotiation) {
                        json(
                            defaultJson(),
                        )
                    }
                    it.install(Logging) {
                        level = LogLevel.HEADERS
                    }
                    it.install(HttpTimeout) {
                        requestTimeoutMillis = 10.seconds.inWholeMilliseconds
                    }
                    it.install(RateLimiterPlugin(1100.milliseconds).plugin)
                },
            )

        val marketItemRepository = StockXMarketItemRepository(catalogApi)
        return MarketPriceComparisonStrategy(marketItemRepository)
    }
}
