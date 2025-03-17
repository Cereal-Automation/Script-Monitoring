package com.cereal.script

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.ExecuteCommandsInteractor
import com.cereal.script.repository.LogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalCoroutinesApi
class ExecuteCommandsInteractorTest {
    private lateinit var logRepository: LogRepository
    private lateinit var interactor: ExecuteCommandsInteractor

    @BeforeEach
    fun setUp() {
        logRepository = mockk(relaxed = true)
        interactor = ExecuteCommandsInteractor(logRepository)
    }

    @Test
    fun `invoke executes all commands in order`() =
        runTest {
            val command1 =
                mockk<Command> {
                    coEvery { shouldRun(any()) } returnsMany listOf(RunDecision.RunOnce(), RunDecision.Skip)
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command 1")
                    }
                    every { getDescription() } returns "Command 1"
                }
            val command2 =
                mockk<Command> {
                    coEvery { shouldRun(any()) } returnsMany listOf(RunDecision.RunOnce(), RunDecision.Skip)
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command 2")
                    }
                    every { getDescription() } returns "Command 2"
                }
            val commands = listOf(command1, command2)
            val startContext = ChainContext()

            val result = interactor(commands, startContext).toList()

            coVerify(exactly = 1) { command1.execute(startContext) }
            coVerify(exactly = 1) { command2.execute(startContext) }
            assertEquals(2, result.size)
        }

    @Test
    fun `invoke skips command based on shouldRun`() =
        runTest {
            val command1 =
                mockk<Command> {
                    coEvery { shouldRun(any()) } returns RunDecision.Skip
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command 1")
                    }
                    every { getDescription() } returns "Command 1"
                }
            val command2 =
                mockk<Command> {
                    coEvery { shouldRun(any()) } returnsMany listOf(RunDecision.RunOnce(), RunDecision.Skip)
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command 2")
                    }
                    every { getDescription() } returns "Command 2"
                }
            val commands = listOf(command1, command2)
            val startContext = ChainContext()

            val result = interactor(commands, startContext).toList()

            coVerify(exactly = 0) { command1.execute(startContext) }
            coVerify(exactly = 1) { command2.execute(startContext) }
            assertEquals(1, result.size)
        }

    @Test
    fun `invoke delays command execution based on shouldRun with delay`() =
        runTest {
            val delayMillis = 100.milliseconds
            val command1 =
                mockk<Command> {
                    coEvery { shouldRun(any()) } returnsMany listOf(RunDecision.RunOnce(delayMillis), RunDecision.Skip)
                    every { getDescription() } returns "Command 1"
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command 1")
                    }
                }

            val commands = listOf(command1)
            val startContext = ChainContext()
            val startTime = System.currentTimeMillis()

            runBlocking {
                interactor(commands, startContext).toList()
            }

            val endTime = System.currentTimeMillis()
            // Allow for some variance due to execution
            assert(endTime - startTime >= delayMillis.inWholeMilliseconds - 10)
            coVerify(exactly = 1) { command1.execute(startContext) }
        }

    @Test
    fun `executeCommand calls withLogging and withRetry`() =
        runTest {
            val command =
                mockk<Command> {
                    every { getDescription() } returns "Test Command"
                    coEvery { execute(any()) } coAnswers {
                        println("Executing command test")
                    }
                }
            val context = ChainContext()

            @Suppress("UNCHECKED_CAST")
            val executedFlow: Flow<ChainContext> =
                interactor.javaClass
                    .getDeclaredMethod("executeCommand", Command::class.java, ChainContext::class.java)
                    .apply { isAccessible = true }
                    .invoke(interactor, command, context) as Flow<ChainContext>
            executedFlow.toList()

            coVerify(exactly = 1) { logRepository.info("Starting Test Command.", any()) }
            coVerify(exactly = 1) { command.execute(any()) }
        }
}
