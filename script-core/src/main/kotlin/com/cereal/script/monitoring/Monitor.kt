package com.cereal.script.monitoring

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.monitoring.data.ScriptLogRepository
import com.cereal.script.monitoring.data.ScriptNotificationRepository
import com.cereal.script.monitoring.data.item.RssFeedItemRepository
import com.cereal.script.monitoring.domain.MonitorInteractor
import com.cereal.script.monitoring.domain.MonitorStrategyFactory
import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Monitor(
    private val scriptId: String,
    private val scriptPublicKey: String?,
    private val monitorMode: MonitorMode,
    private val dataSource: DataSource,
) {
    private var isLicensed = false

    suspend fun onStart(provider: ComponentProvider): Boolean {
        val scriptId = scriptId
        val scriptPublicKey = scriptPublicKey

        if (scriptPublicKey != null) {
            val licenseChecker = LicenseChecker(scriptId, scriptPublicKey, provider.license())
            val licenseResult = licenseChecker.checkAccess()
            isLicensed = licenseResult is LicenseState.Licensed

            // When there was an error during license validation (i.e. no internet connection or server was down)
            // we want the user to be able to restart the script so license can be checked again so only in that
            // case return false. This stops the script and the user can start it again manually.
            return licenseResult !is LicenseState.ErrorValidatingLicense
        } else {
            isLicensed = true
            return true
        }
    }

    suspend fun execute(
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
        sleep: Duration? = null,
    ): ExecutionResult {
        // Prevent execution when user is not licensed.
        if (!isLicensed) {
            return ExecutionResult.Error("Unlicensed")
        }

        try {
            val interactor = createInteractor(provider, statusUpdate)
            interactor(MonitorInteractor.Config(monitorMode))
        } catch (e: Exception) {
            statusUpdate("An error occurred with message: ${e.message}")
            return ExecutionResult.Error("Error while monitoring")
        }

        val delay =
            max(MIN_INTERVAL_DURATION.inWholeMilliseconds, (sleep ?: DEFAULT_INTERVAL_DURATION).inWholeMilliseconds)
        return ExecutionResult.Loop("Finished check, looping...", delay)
    }

    fun onFinish() {
        // No-op
    }

    companion object {
        val DEFAULT_INTERVAL_DURATION = 5.seconds
        val MIN_INTERVAL_DURATION = 1.seconds
    }

    private fun createInteractor(
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): MonitorInteractor {
        val notificationRepository = ScriptNotificationRepository(provider.preference(), provider.notification())
        val logRepository = ScriptLogRepository(provider.logger(), statusUpdate)
        val monitorStrategyFactory = MonitorStrategyFactory()
        val monitorRepository =
            when (val source = dataSource) {
                is DataSource.RssFeed -> RssFeedItemRepository(source.rssFeedUrl, provider.logger())
            }

        return MonitorInteractor(
            monitorStrategyFactory,
            monitorRepository,
            notificationRepository,
            logRepository,
        )
    }
}
