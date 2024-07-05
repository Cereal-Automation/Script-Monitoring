import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.sample.SampleConfiguration
import com.cereal.script.sample.SampleScript
import com.cereal.sdk.models.proxy.Proxy
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class TestSampleScript {

    @Test
    fun testSuccess() = runBlocking {
        // Initialize script and the test script runner.
        val script = SampleScript()
        val scriptRunner = TestScriptRunner(script)

        // Mock the LicenseChecker
        mockkConstructor(LicenseChecker::class)
        coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

        // Mock the configuration values
        val configuration = mockk<SampleConfiguration> {
            every { nullableStringValue() } returns null
            every { booleanValue() } returns true
            every { integerValue() } returns 100
            every { floatValue() } returns 101.0f
            every { doubleValue() } returns 102.0
            every { proxyValue() } returns Proxy(id="some-id", address = "10.0.0.0", port = 10000)
            every { usernameValue() } returns "MyUsername"
            every { passwordValue() } returns "MyPassword"
        }
        val componentProviderFactory = TestComponentProviderFactory()

        try {
            // Run the script with a 10s timeout. This is needed because most scripts don't end within a reasonable time.
            // If your script is expected to end automatically please remove the surrounding try catch block.
            withTimeout(10000) { scriptRunner.run(configuration, componentProviderFactory) }
        } catch(e: Exception) {
            // Ignore timeouts because they're expected.
        }
    }

}
