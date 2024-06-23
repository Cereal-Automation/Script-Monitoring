package com.cereal.script.monitoring

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.monitoring.domain.MonitorInteractor
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.models.Value
import com.cereal.script.monitoring.domain.repository.ItemMonitorRepository
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


abstract class MonitoringScript<T : MonitoringConfiguration> : Script<T> {

    abstract val scriptId: String?
    abstract val scriptPublicKey: String?
    abstract val monitorMode: MonitorMode

    abstract fun getItemMonitorRepository(): ItemMonitorRepository

    private var isLicensed = false

    private lateinit var interactor: MonitorInteractor
    private var job: Job? = null

    override suspend fun onStart(configuration: T, provider: ComponentProvider): Boolean {
        interactor = MonitorInteractor(getItemMonitorRepository())

        val scriptId = scriptId
        val scriptPublicKey = scriptPublicKey

        if(scriptId != null && scriptPublicKey != null) {
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

    override suspend fun execute(configuration: T, provider: ComponentProvider, statusUpdate: suspend (message: String) -> Unit): ExecutionResult {
        // Prevent execution when user is not licensed.
        if(!isLicensed) {
            return ExecutionResult.Error("Unlicensed")
        }

        statusUpdate("Start monitoring ...")

        job = CoroutineScope(Dispatchers.Default).launch {
            interactor(MonitorInteractor.Config(monitorMode)).collect {
                statusUpdate(it.getStatusText(monitorMode))
                if (it.notify) {
                    statusUpdate("Sending notification for '${it.item.name}'.")
                    // TODO: Notify
                }
            }
        }

        return ExecutionResult.Loop("Finished check, looping...", 5000)
    }

    override suspend fun onFinish(configuration: T, provider: ComponentProvider) {
        job?.cancel()
    }

    private fun MonitorInteractor.MonitoredItem.getStatusText(mode: MonitorMode): String {
        return when(mode) {
            is MonitorMode.NewItem -> "Found item ${item.name}"
            is MonitorMode.EqualsOrBelowPrice -> {
                val price = (item.values?.firstOrNull { it is Value.Price } as Value.Price?)?.value
                "Found item '${item.name}' with price $price."
            }
        }
    }
}
