package com.cereal.script.monitoring

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.monitoring.domain.MonitorInteractor
import com.cereal.script.monitoring.domain.models.*
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


abstract class MonitoringScript<T: ScriptConfiguration> : Script<T> {

    abstract val scriptId: String?
    abstract val scriptPublicKey: String?
    abstract val monitorMode: MonitorMode
    abstract val dataSource: DataSource

    private var isLicensed = false

    private var job: Job? = null
    private lateinit var monitorFactory: MonitorFactory

    override suspend fun onStart(configuration: T, provider: ComponentProvider): Boolean {
        monitorFactory = MonitorFactory(provider, dataSource)

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

        val interactor = monitorFactory.createInteractor(statusUpdate)
        job = CoroutineScope(Dispatchers.Default).launch {
            try {
                interactor(MonitorInteractor.Config(monitorMode))
            }
            catch(e: MissingValueTypeException) {
                // TODO
            }
        }

        return ExecutionResult.Loop("Finished check, looping...", 5000)
    }

    override suspend fun onFinish(configuration: T, provider: ComponentProvider) {
        job?.cancel()
    }

}
