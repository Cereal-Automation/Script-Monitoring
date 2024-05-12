import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.sample.SampleConfiguration
import com.cereal.script.sample.SampleScript
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
            every { keyString() } returns "Some random string"
            every { keyBoolean() } returns true
            every { keyInteger() } returns 100
            every { keyFloat() } returns 101.0f
            every { keyDouble() } returns 102.0
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
